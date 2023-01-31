package com.rydzwr.tictactoe.service.game.strategy.gameState;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.outgoing.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.PlayerMoveResponseDto;

import java.util.List;

public interface GameStateStrategy {
    void send(PlayerMoveResponseDto moves, Game game, Player player, int playerMoveIndex);
    boolean applies(CheckWinState checkWinState);
}
