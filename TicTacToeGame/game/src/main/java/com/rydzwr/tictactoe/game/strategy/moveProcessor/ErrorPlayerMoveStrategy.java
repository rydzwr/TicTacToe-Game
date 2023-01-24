package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class ErrorPlayerMoveStrategy implements ProcessMoveStrategy{
    @Override
    public Game processPlayerMove(Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto) {
        throw  new IllegalArgumentException(GameConstants.INVALID_PLAYER_TYPE_EXCEPTION);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return !playerType.equals(PlayerType.ONLINE) && !playerType.equals(PlayerType.LOCAL) && !playerType.equals(PlayerType.AI);
    }
}
