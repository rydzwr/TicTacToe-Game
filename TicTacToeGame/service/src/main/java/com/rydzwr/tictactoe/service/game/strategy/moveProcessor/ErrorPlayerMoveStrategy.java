package com.rydzwr.tictactoe.service.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class ErrorPlayerMoveStrategy implements ProcessMoveStrategy{
    @Override
    public void processPlayerMove(PlayerMoveResponseDto moves, GameAdapter gameAdapter, SimpMessageHeaderAccessor accessor, MoveCoordsDto moveCoordsDto) {
        throw  new IllegalArgumentException(GameConstants.INVALID_PLAYER_TYPE_EXCEPTION);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return !playerType.equals(PlayerType.ONLINE) && !playerType.equals(PlayerType.LOCAL) && !playerType.equals(PlayerType.AI);
    }
}
