package com.rydzwr.tictactoe.service.game.strategy.gameState;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.GameResultDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.GameService;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WinGameStrategy implements GameStateStrategy {
    private final GameService gameService;

    @Override
    public Object resolve(PlayerMoveResponseDto moves, GameAdapter gameAdapter, Player player, MoveCoordsDto moveCoordsDto) {
        gameService.deleteFinishedGame(gameAdapter.getGame());
        return new GameStateDto(GameState.FINISHED.name(), new GameResultDto("WIN", player.getPawn()));
    }

    @Override
    public boolean applies(CheckWinState checkWinState) {
        return checkWinState.equals(CheckWinState.WIN);
    }
}
