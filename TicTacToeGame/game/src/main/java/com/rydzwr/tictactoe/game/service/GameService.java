package com.rydzwr.tictactoe.game.service;

import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.GameDto;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.database.dto.outgoing.LoadGameDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.database.service.PlayerDatabaseService;
import com.rydzwr.tictactoe.database.service.UserDatabaseService;
import com.rydzwr.tictactoe.game.algorithm.CheckWinAlgorithm;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.selector.GameBuilderStrategySelector;
import com.rydzwr.tictactoe.game.selector.PlayerPawnRandomSelector;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.BuildGameStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameBuilderStrategySelector selector;
    private final GameDatabaseService gameDatabaseService;
    private final UserDatabaseService userDatabaseService;
    private final PlayerDatabaseService playerDatabaseService;
    private final CheckWinAlgorithm checkWinAlgorithm;

    public Game buildGame(GameDto gameDto) {
        BuildGameStrategy strategy = selector.chooseStrategy(gameDto);
        Game game = strategy.buildGame(gameDto);
        gameDatabaseService.save(game);
        return gameDatabaseService.findById(game.getId());
    }

    public String getInviteCode(String callerName) {
        User user = userDatabaseService.findByName(callerName);
        Player player = playerDatabaseService.findFirstByUser(user);
        return player.getGame().getInviteCode();
    }

    public Player retrieveAnyPlayerFromUser(SimpMessageHeaderAccessor accessor) {
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        User user = userDatabaseService.findByName(username);
        return playerDatabaseService.findFirstByUser(user);
    }

    public boolean checkWin(Game game, int playerMoveIndex) {
        return checkWinAlgorithm.checkWin(game, playerMoveIndex);
    }

    @Transactional
    public void deleteFinishedGame(Game game) {
        gameDatabaseService.delete(game);
    }

    public Player getCurrentPlayer(Game game) {
        return game.getPlayers().get(game.getCurrentPlayerTurn());
    }

    public boolean isNextPlayerAIType(Game game) {
        return getCurrentPlayer(game).getPlayerType().equals(PlayerType.AI);
    }

    public boolean isUserInGame() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User caller = userDatabaseService.findByName(userName);
        return playerDatabaseService.existsByUser(caller);
    }

    public LoadGameDto loadPreviousPlayerGame() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User caller = userDatabaseService.findByName(userName);
        Player player = playerDatabaseService.findFirstByUser(caller);
        Game game = player.getGame();
        return new LoadGameDto(game, player.getPawn(), getCurrentPlayer(game).getPawn());
    }

    @Transactional
    public void removePrevUserGame(String username) {
        User caller = userDatabaseService.findByName(username);
        Player player = playerDatabaseService.findFirstByUser(caller);
        gameDatabaseService.deleteById(player.getGame().getId());
    }

    // TODO SPLIT INTO TWO METHODS

    @Transactional
    public LoadGameDto addPlayerToOnlineGame(String callerName, String inviteCode, SimpMessagingTemplate template) {
        final String AWAITING_PLAYERS_ENDPOINT = "/topic/awaitingPlayers";
        final String GAME_STATE_ENDPOINT = "/topic/gameState";
        final char X_PAWN = 'X';

        PlayerPawnRandomSelector playerPawnRandomSelector = new PlayerPawnRandomSelector();
        User caller = userDatabaseService.findByName(callerName);

        if (!gameDatabaseService.existsByInviteCode(inviteCode)) {
            throw new IllegalArgumentException(GameConstants.GAME_WITH_GIVEN_CODE_NOT_FOUND_EXCEPTION);
        }

        Game game = gameDatabaseService.findByInviteCode(inviteCode);
        char availablePawn = playerPawnRandomSelector.selectAvailablePawn(game);

        int availableGameSlots = countEmptyGameSlots(game);

        if (availableGameSlots == 0) {
            throw new IllegalArgumentException(GameConstants.ALL_GAME_SLOTS_OCCUPIED_EXCEPTION);
        }

        Player newPlayer = new PlayerBuilder()
                .setPlayerGameIndex(game.getPlayers().size())
                .setPlayerType(PlayerType.ONLINE)
                .setPlayerPawn(availablePawn)
                .setUser(caller)
                .setGame(game)
                .build();
        availableGameSlots--;

        playerDatabaseService.save(newPlayer);
        template.convertAndSend(AWAITING_PLAYERS_ENDPOINT, availableGameSlots);

        if (availableGameSlots == 0) {
            game.setState(GameState.IN_PROGRESS);
            gameDatabaseService.save(game);
            template.convertAndSend(GAME_STATE_ENDPOINT, new GameStateDto(GameState.IN_PROGRESS.name()));
        }

        return new LoadGameDto(game, availablePawn, X_PAWN, availableGameSlots);
    }

    // TODO THAT SHOULD BE IN GAME DTO CLASS

    public int getHumanGameSlots(GameDto game) {

        int allGameSlots = game.getPlayers().size();

        int aIPlayersCount = (int) game.getPlayers().stream()
                .filter((playerDto -> playerDto.getPlayerType().equals(PlayerType.AI.name())))
                .count();

        return allGameSlots - aIPlayersCount;
    }

    // TODO THAT SHOULD BE IN GAME ENTITY CLASS

    public int countEmptyGameSlots(Game game) {
        int gamePlayerCount = game.getPlayersCount();
        int occupiedSlotsCount = game.getPlayers().size();
        return gamePlayerCount - occupiedSlotsCount;
    }

    // TODO SHOULD BE IN GAME BOARD CLASS
    public boolean containsEmptyFields(Game game) {
        return game.getGameBoard().contains("-");
    }
}
