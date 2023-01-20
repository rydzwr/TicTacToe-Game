package com.rydzwr.tictactoe.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.game.constants.GameConstants;

public class ErrorGameTypeStrategy implements BuildGameStrategy {
    @Override
    public void buildGame(GameDto gameDto) {
        throw new IllegalArgumentException(GameConstants.INVALID_GAME_TYPE_EXCEPTION);
    }

    @Override
    public boolean applies(GameDto gameDto) {
        return false;
    }
}
