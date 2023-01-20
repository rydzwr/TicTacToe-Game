package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;

public interface ProcessMoveStrategy {

    public Game processPlayerMove(Game game, PlayerMoveDto playerMoveDto);

    public boolean applies(PlayerType playerType);
}
