package com.rydzwr.tictactoe.web.strategy;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.web.constants.CheckWinState;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface GameStateStrategy {
    public void send(Game game, Player player, SimpMessagingTemplate template);
    public boolean applies(CheckWinState checkWinState);
}
