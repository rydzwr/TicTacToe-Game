package com.rydzwr.tictactoe.game.strategyforgame;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.game.constants.GameConstants;

public class ErrorGameTypeStrategy implements BuildGameStrategy {
    @Override
    public Game buildGame(GameDto gameDto) {
        throw new IllegalArgumentException(GameConstants.INVALID_GAME_TYPE_EXCEPTION);
    }

    @Override
    public boolean applies(String gameType) {
        return !GameConstants.validGameTypeNames.contains(gameType) || gameType == null;
    }
}
