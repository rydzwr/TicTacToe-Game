package com.rydzwr.tictactoe.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.dto.incoming.GameDto;
import com.rydzwr.tictactoe.database.model.Game;

public interface BuildGameStrategy {
    public Game buildGame(GameDto gameDto);
    public boolean applies(GameDto gameDto);
}
