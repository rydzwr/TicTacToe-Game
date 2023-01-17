package com.rydzwr.tictactoe.game.strategyforgame;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.game.constants.GameConstants;

public class AIOpponentsGameStrategy implements BuildGameStrategy{

    @Override
    public Game buildGame(GameDto gameDto) {
        return null;
    }

    @Override
    public boolean applies(String gameType) {
        return gameType.equals(GameConstants.AI_OPPONENTS_GAME_NAME);    }
}