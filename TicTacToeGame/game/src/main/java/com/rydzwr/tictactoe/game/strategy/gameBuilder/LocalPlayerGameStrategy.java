package com.rydzwr.tictactoe.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.builder.GameBuilder;
import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.GameDto;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.database.service.PlayerDatabaseService;
import com.rydzwr.tictactoe.database.service.UserDatabaseService;
import com.rydzwr.tictactoe.game.selector.PlayerPawnRandomSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalPlayerGameStrategy implements BuildGameStrategy {
    private final GameDatabaseService gameDatabaseService;
    private final UserDatabaseService userDatabaseService;
    private final PlayerDatabaseService playerDatabaseService;

    @Override
    @Transactional
    public Game buildGame(GameDto gameDto) {
        Game game = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty())
                .setGameState(GameState.IN_PROGRESS)
                .build();

        gameDatabaseService.save(game);

        User caller = userDatabaseService.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        assert caller != null;

        Player callerPlayer = new PlayerBuilder()
                .setPlayerType(PlayerType.LOCAL)
                .setPlayerPawn('X')
                .setUser(caller)
                .setGame(game)
                .build();

        playerDatabaseService.save(callerPlayer);

        PlayerPawnRandomSelector playerPawnRandomSelector = new PlayerPawnRandomSelector();
        for (PlayerDto playerDto : gameDto.getPlayers()) {
            char pawn = playerPawnRandomSelector.selectPawn();
            Player player = new PlayerBuilder()
                    .setPlayerType(playerDto)
                    .setPlayerPawn(pawn)
                    .setUser(caller)
                    .setGame(game)
                    .build();
            playerDatabaseService.save(player);
        }
        gameDatabaseService.save(game);
        return game;
    }


    @Override
    public boolean applies(GameDto gameDto) {
        List<String> playerTypes = gameDto.getPlayers().stream().map(PlayerDto::getPlayerType).toList();
        return playerTypes.contains("LOCAL") || playerTypes.contains("AI") && !playerTypes.contains("ONLINE");
    }
}
