package com.rydzwr.tictactoe.service.game;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.LoadGameDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.algorithm.CheckWinAlgorithm;
import com.rydzwr.tictactoe.service.game.builder.PlayerBuilder;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import com.rydzwr.tictactoe.service.game.database.PlayerDatabaseService;
import com.rydzwr.tictactoe.service.game.strategy.selector.GameBuilderStrategySelector;
import com.rydzwr.tictactoe.service.game.strategy.selector.PlayerPawnRandomSelector;
import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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

    @Transactional
    public Game buildGame(GameDto gameDto) {
        var strategy = selector.chooseStrategy(gameDto);
        var game = strategy.buildGame(gameDto);
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

    public boolean checkWin(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkWinAlgorithm.checkWin(gameAdapter, moveCoordsDto);
    }

    @Transactional
    public void deleteFinishedGame(Game game) {
        gameDatabaseService.delete(game);
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
        var currentPlayer = new GameAdapter(game).getCurrentPlayer();

        return new LoadGameDto(game, player.getPawn(), currentPlayer.getPawn());
    }

    @Transactional
    public void removePrevUserGame(String username) {
        User caller = userDatabaseService.findByName(username);
        Player player = playerDatabaseService.findFirstByUser(caller);
        gameDatabaseService.deleteById(player.getGame().getId());
    }

    @Transactional
    public LoadGameDto addPlayerToOnlineGame(String callerName, String inviteCode) {
        var playerPawnRandomSelector = new PlayerPawnRandomSelector();
        User caller = userDatabaseService.findByName(callerName);

        if (!gameDatabaseService.existsByInviteCode(inviteCode)) {
            throw new IllegalArgumentException(GameConstants.GAME_WITH_GIVEN_CODE_NOT_FOUND_EXCEPTION);
        }

        Game game = gameDatabaseService.findByInviteCode(inviteCode);
        char availablePawn = playerPawnRandomSelector.selectAvailablePawn(game);

        int availableGameSlots = new GameAdapter(game).countEmptyGameSlots();

        if (availableGameSlots == 0) {
            throw new IllegalArgumentException(GameConstants.ALL_GAME_SLOTS_OCCUPIED_EXCEPTION);
        }

        var newPlayer = new PlayerBuilder()
                .setPlayerGameIndex(game.getPlayers().size())
                .setPlayerType(PlayerType.ONLINE)
                .setPlayerPawn(availablePawn)
                .setUser(caller)
                .setGame(game)
                .build();
        availableGameSlots--;

        playerDatabaseService.save(newPlayer);

        if (availableGameSlots == 0) {
            game.setState(GameState.IN_PROGRESS);
            gameDatabaseService.save(game);
        }

        return new LoadGameDto(game, availablePawn, GameConstants.DEFAULT_STARTING_PAWN, availableGameSlots);
    }
}
