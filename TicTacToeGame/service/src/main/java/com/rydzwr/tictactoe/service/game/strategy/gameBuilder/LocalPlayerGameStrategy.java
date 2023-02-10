package com.rydzwr.tictactoe.service.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.builder.GameBuilder;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalPlayerGameStrategy implements BuildGameStrategy {
    private final GameDatabaseService gameDatabaseService;
    private final GameBuilderService gameBuilderService;

    @Override
    @Transactional
    public Game buildGame(GameDto gameDto, String callerName) {

        var caller = gameBuilderService.getCaller(callerName);
        assert caller != null;

        Game game = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty())
                .setGameState(GameState.IN_PROGRESS)
                .build();

        gameDatabaseService.save(game);

        gameBuilderService.buildCallerPlayer(caller, game, PlayerType.LOCAL);
        gameBuilderService.buildLocalPlayers(game, gameDto);

        gameDatabaseService.save(game);
        return game;
    }


    @Override
    public boolean applies(GameDto gameDto) {
        List<String> playerTypes = gameDto.getPlayers().stream().map(PlayerDto::getPlayerType).toList();
        return playerTypes.contains("LOCAL") || playerTypes.contains("AI") && !playerTypes.contains("ONLINE");
    }
}
