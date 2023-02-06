package com.rydzwr.tictactoe.service.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface ProcessMoveStrategy {

    void processPlayerMove(PlayerMoveResponseDto moves, GameAdapter gameAdapter, SimpMessageHeaderAccessor accessor, MoveCoordsDto moveCoordsDto);

    boolean applies(PlayerType playerType);
}
