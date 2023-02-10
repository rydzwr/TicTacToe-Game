package com.rydzwr.tictactoe.service.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;

import java.util.List;

public class ErrorGameTypeStrategy implements BuildGameStrategy {
    @Override
    public Game buildGame(GameDto gameDto, String callerName) {
        throw new IllegalArgumentException(GameConstants.INVALID_GAME_TYPE_EXCEPTION);
    }

    @Override
    public boolean applies(GameDto gameDto) {
        List<String> playerTypes = gameDto.getPlayers().stream().map(PlayerDto::getPlayerType).toList();
        return !playerTypes.contains("ONLINE") || !playerTypes.contains("LOCAL");
    }
}
