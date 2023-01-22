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
    public void buildGame(GameDto gameDto) {
        BuildGameStrategy strategy = selector.chooseStrategy(gameDto);
        strategy.buildGame(gameDto);
    }

    public String getInviteCode(String callerName) {
        User user = userDatabaseService.findByName(callerName);
        Player player = playerDatabaseService.findFirstByUser(user);
        return player.getGame().getInviteCode();
    }

    public Player retrieveCaller(SimpMessageHeaderAccessor accessor) {
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
    public void addPlayerToOnlineGame(String callerName, String inviteCode) {
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
    }

    public int getEmptyGameSlots(String callerName) {
        User caller = userDatabaseService.findByName(callerName);
        Player player = playerDatabaseService.findFirstByUser(caller);
        Game game = player.getGame();

        log.info("----------------------------------------");
        log.info("GAME SERVICE: --> (getEmptyGameSlots)");
        log.info("CALLER: --> {}", caller.getName());
        log.info("PLAYER: --> {}", player.getPawn());
        log.info("GAME: --> {}", game.getId());
        log.info("----------------------------------------");

        int allGameSlots = game.getPlayersCount();

        int onlinePlayersCount = getOnlinePlayersCount(game);
        int aIPlayersCount = getAIPlayersCount(game);

        int occupiedSlots = onlinePlayersCount + aIPlayersCount;

        int emptyGameSlots = allGameSlots - occupiedSlots;

        log.info("----------------------------------------");
        log.info("GAME SERVICE: --> (getEmptyGameSlots)");
        log.info("ALL GAME SLOTS: --> {}", allGameSlots);
        log.info("AI PLAYERS COUNT IN GAME DTO: --> {}", aIPlayersCount);
        log.info("ONLINE PLAYERS COUNT IN GAME DTO: --> {}", onlinePlayersCount);
        log.info("RETURNS EMPTY GAME SLOTS COUNT: --> {}", emptyGameSlots);
        log.info("----------------------------------------");

        return emptyGameSlots;
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
