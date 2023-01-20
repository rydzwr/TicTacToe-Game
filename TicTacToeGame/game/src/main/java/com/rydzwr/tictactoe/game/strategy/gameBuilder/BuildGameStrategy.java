package com.rydzwr.tictactoe.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.dto.GameDto;

public interface BuildGameStrategy {
    public void buildGame(GameDto gameDto);
    public boolean applies(GameDto gameDto);
}
