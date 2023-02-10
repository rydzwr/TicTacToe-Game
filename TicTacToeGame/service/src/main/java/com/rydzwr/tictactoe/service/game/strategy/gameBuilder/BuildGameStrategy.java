package com.rydzwr.tictactoe.service.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;

public interface BuildGameStrategy {
    Game buildGame(GameDto gameDto, String callerName);
    boolean applies(GameDto gameDto);
}
