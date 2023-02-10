package com.rydzwr.tictactoe.service.game.validator;

import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.game.GameService;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlayerMoveValidator {
    private final GameService gameService;

    public boolean validatePlayerMove(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        var gameBoard = gameAdapter.getGameBoardCopy();
        var moveIndex = moveCoordsDto.getIndex(gameAdapter.getGameSize());
        if (moveIndex > gameBoard.length()) {
            throw new IllegalArgumentException(GameConstants.PLAYER_MOVE_OUT_OF_BOARD_EXCEPTION);
        }
        return gameBoard.charAt(moveIndex) != '-';
    }
    public boolean validateCurrentPlayerTurn(GameAdapter gameAdapter, SimpMessageHeaderAccessor accessor) {
        var callerPlayer = gameService.retrieveAnyPlayerFromUser(accessor);
        var currentPlayer = gameAdapter.getCurrentPlayer();
        return callerPlayer.getPawn() == currentPlayer.getPawn();
    }
}
