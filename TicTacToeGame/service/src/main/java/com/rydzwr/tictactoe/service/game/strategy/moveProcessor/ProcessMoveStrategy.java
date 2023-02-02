package com.rydzwr.tictactoe.service.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.service.dto.outgoing.PlayerMoveResponseDto;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.List;

public interface ProcessMoveStrategy {

    void processPlayerMove(PlayerMoveResponseDto moves, Game game, SimpMessageHeaderAccessor accessor, int moveIndex);

    boolean applies(PlayerType playerType);
}
