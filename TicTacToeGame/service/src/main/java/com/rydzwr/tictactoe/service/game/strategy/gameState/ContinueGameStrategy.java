package com.rydzwr.tictactoe.service.game.strategy.gameState;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContinueGameStrategy implements GameStateStrategy {

    @Override
    public Object resolve(PlayerMoveResponseDto moves, GameAdapter gameAdapter, Player player, MoveCoordsDto moveCoordsDto) {
        var nextPlayer = gameAdapter.getCurrentPlayer();
        moves.setCurrentPlayerMove(nextPlayer.getPawn());
        return moves;
    }

    @Override
    public boolean applies(CheckWinState checkWinState) {
        return checkWinState.equals(CheckWinState.CONTINUE);
    }
}
