package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;

public class ErrorPlayerMoveStrategy implements ProcessMoveStrategy{
    @Override
    public Game processPlayerMove(Game game, PlayerMoveDto playerMoveDto) {
        throw  new IllegalArgumentException(GameConstants.PLAYER_MOVE_OUT_OF_BOARD_EXCEPTION);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return false;
    }
}
