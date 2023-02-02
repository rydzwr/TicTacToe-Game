package com.rydzwr.tictactoe.service.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.service.dto.outgoing.PlayerMoveResponseDto;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class ErrorPlayerMoveStrategy implements ProcessMoveStrategy{
    @Override
    public void processPlayerMove(PlayerMoveResponseDto moves, Game game, SimpMessageHeaderAccessor accessor, int moveIndex) {
        throw  new IllegalArgumentException(GameConstants.INVALID_PLAYER_TYPE_EXCEPTION);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return !playerType.equals(PlayerType.ONLINE) && !playerType.equals(PlayerType.LOCAL) && !playerType.equals(PlayerType.AI);
    }
}
