package com.rydzwr.tictactoe.service.game.strategy.gameState;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;

public interface GameStateStrategy {

    Object resolve(PlayerMoveResponseDto moves, GameAdapter gameAdapter, Player player, MoveCoordsDto moveCoordsDto);
    boolean applies(CheckWinState checkWinState);
}
