package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface ProcessMoveStrategy {

    public Game processPlayerMove(Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto);

    public boolean applies(PlayerType playerType);
}
