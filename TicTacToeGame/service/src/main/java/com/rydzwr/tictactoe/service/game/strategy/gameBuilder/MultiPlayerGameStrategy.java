package com.rydzwr.tictactoe.service.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.algorithm.InviteCodeGenerator;
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
public class MultiPlayerGameStrategy implements BuildGameStrategy {
    private final GameDatabaseService gameDatabaseService;
    private final InviteCodeGenerator inviteCodeGenerator;
    private final GameBuilderService gameBuilderService;

    @Override
    @Transactional
    public Game buildGame(GameDto gameDto) {
        String inviteCode = inviteCodeGenerator.generateCode();

        Game game = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty())
                .setPlayersCount(gameDto.getPlayers().size())
                .setGameState(GameState.AWAITING_PLAYERS)
                .setInviteCode(inviteCode)
                .build();

        gameDatabaseService.save(game);

        var caller = gameBuilderService.getCaller();
        assert caller != null;

        int aiPlayersCount = gameDto.countAIPlayers();

        gameBuilderService.buildCallerPlayer(caller, game, PlayerType.ONLINE);
        gameBuilderService.buildAIPlayers(game, aiPlayersCount);

        gameDatabaseService.save(game);
        return game;
    }

    @Override
    public boolean applies(GameDto gameDto) {
        List<String> playerTypes = gameDto.getPlayers().stream().map(PlayerDto::getPlayerType).toList();
        return playerTypes.contains("ONLINE");
    }
}