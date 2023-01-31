package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.database.model.Game;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.List;

public interface ProcessMoveStrategy {

    public void processPlayerMove(PlayerMoveResponseDto moves, Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto);

    public boolean applies(PlayerType playerType);
}
