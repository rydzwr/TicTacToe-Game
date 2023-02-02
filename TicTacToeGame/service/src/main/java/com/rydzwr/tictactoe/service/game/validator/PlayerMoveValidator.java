package com.rydzwr.tictactoe.service.game.validator;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.service.game.GameService;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlayerMoveValidator {
    private final GameService gameService;

    public boolean validatePlayerMove(String newGameBoard, int moveIndex) {
        if (moveIndex > newGameBoard.length()) {
            throw new IllegalArgumentException(GameConstants.PLAYER_MOVE_OUT_OF_BOARD_EXCEPTION);
        }
        return newGameBoard.charAt(moveIndex) != '-';
    }
    public boolean validateCurrentPlayerTurn(Game game, SimpMessageHeaderAccessor accessor) {
        Player currentPlayer = new GameAdapter(game).getCurrentPlayer();
        Player callerPlayer = gameService.retrieveAnyPlayerFromUser(accessor);
        return callerPlayer.getPawn() == currentPlayer.getPawn();
    }
}