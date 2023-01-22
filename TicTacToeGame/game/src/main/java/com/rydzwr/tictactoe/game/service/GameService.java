package com.rydzwr.tictactoe.game.service;

import com.rydzwr.tictactoe.database.builder.GameBuilder;
import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.LoadGameDto;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        return strategy.buildGame(gameDto);
    }

    public String getInviteCode(String callerName) {
        User user = userDatabaseService.findByName(callerName);
        Player player = playerDatabaseService.findFirstByUser(user);
        return player.getGame().getInviteCode();
    }

    public User retrieveCallerUser(SimpMessageHeaderAccessor accessor) {
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        return userDatabaseService.findByName(username);
    }

    public Player retrieveCallerPlayer(SimpMessageHeaderAccessor accessor) {
        // GETTING USER FROM SECURITY CONTEXT
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        User user = userDatabaseService.findByName(username);

        // IT'S BARELY POSSIBLE BUT CHECKING JUST IN CASE
        assert user != null;

        // FINDING HIS (ONE -> TO -> ONE) PLAYER
        return playerDatabaseService.findFirstByUser(user);
    }

    public boolean checkWin(Game game) {
        return checkWinAlgorithm.checkWin(game);
    }

    public void processGameWinning(Game game) {
        log.info("GAME SERVICE: --> Game Has Been Won, Processing Delete Entity");
        gameDatabaseService.delete(game);
    }

    public Player getCurrentPlayer(Game game) {
        return game.getPlayers().get(game.getCurrentPlayerTurn());
    }

    public boolean isPlayerAIType(Game game) {
        boolean isAi = getCurrentPlayer(game).getPlayerType().equals(PlayerType.AI);
        log.info("GAME SERVICE: --> Is Player AI: {}", isAi);
        return isAi;
    }

    public boolean isUserInGame() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User caller = userDatabaseService.findByName(userName);
        return playerDatabaseService.existsByUser(caller);
    }

    public boolean validatePlayerMove(String newGameBoard, PlayerMoveDto playerMoveDto) {
        if (playerMoveDto.getGameBoardElementIndex() > newGameBoard.length()) {
            throw new IllegalArgumentException(GameConstants.PLAYER_MOVE_OUT_OF_BOARD_EXCEPTION);
        }

        return newGameBoard.charAt(playerMoveDto.getGameBoardElementIndex()) == '-';
    }

    public int updateCurrentPlayerTurn(List<Player> players, int currentPlayerTurn) {
        log.info("GAME SERVICE: --> Updating Current Player Turn In Entity");
        return currentPlayerTurn == players.size() - 1 ? 0 : currentPlayerTurn + 1;
    }

    public LoadGameDto loadPreviousPlayerGame() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User caller = userDatabaseService.findByName(userName);
        Player player = playerDatabaseService.findFirstByUser(caller);
        Game game = player.getGame();
        return new LoadGameDto(game.getGameBoard(), getCurrentPlayer(game).getPawn(), game.getGameSize());
    }

    @Transactional
    public void removePrevUserGame(String username) {
        User caller = userDatabaseService.findByName(username);
        Player player = playerDatabaseService.findFirstByUser(caller);
        gameDatabaseService.delete(player.getGame());
    }

    @Transactional
    public LoadGameDto addPlayerToOnlineGame(String callerName, String inviteCode) {

        log.info("------------------------------------------------");
        log.info("ADD PLAYER TO ONLINE GAME: --> ");
        log.info("CALLER NAME: --> {} ", callerName);
        log.info("INVITE CODE: --> {} ", inviteCode);


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
                .setPlayerType(PlayerType.ONLINE)
                .setPlayerPawn(availablePawn)
                .setUser(caller)
                .setGame(game)
                .build();

        playerDatabaseService.save(newPlayer);
        log.info("------------------------------------------------");
        return new LoadGameDto(game.getGameSize(), availablePawn);
    }

    public int getEmptyGameSlots(String callerName) {
        User caller = userDatabaseService.findByName(callerName);
        Player player = playerDatabaseService.findFirstByUser(caller);
        Game game = player.getGame();

        int allGameSlots = game.getPlayersCount();

        int onlinePlayersCount = getOnlinePlayersCount(game);
        int aIPlayersCount = getAIPlayersCount(game);

        int occupiedSlots = onlinePlayersCount + aIPlayersCount;

        return allGameSlots - occupiedSlots;
    }

    private int getOnlinePlayersCount(Game game) {
        return  (int) game.getPlayers().stream()
                .filter((playerDto -> playerDto.getPlayerType().equals(PlayerType.ONLINE)))
                .count();
    }

    private int getAIPlayersCount(Game game) {
        return  (int) game.getPlayers().stream()
                .filter((playerDto -> playerDto.getPlayerType().equals(PlayerType.AI)))
                .count();
    }

    private int countEmptyGameSlots(Game game) {
        int gamePlayerCount = game.getPlayersCount();
        int occupiedSlotsCount = game.getPlayers().size();
        return gamePlayerCount - occupiedSlotsCount;
    }
}
