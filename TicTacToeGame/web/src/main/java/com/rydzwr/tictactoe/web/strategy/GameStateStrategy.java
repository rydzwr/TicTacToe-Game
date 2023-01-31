package com.rydzwr.tictactoe.web.strategy;

import com.rydzwr.tictactoe.database.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.web.constants.CheckWinState;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

public interface GameStateStrategy {
    public void send(PlayerMoveResponseDto moves, Game game, Player player, int playerMoveIndex, SimpMessagingTemplate template);
    public boolean applies(CheckWinState checkWinState);
}
