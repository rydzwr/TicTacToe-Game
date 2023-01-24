package com.rydzwr.tictactoe.game.validator;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlayerMoveValidator {
    private final GameService gameService;

    public boolean validatePlayerMove(String newGameBoard, PlayerMoveDto playerMoveDto) {
        if (playerMoveDto.getGameBoardElementIndex() > newGameBoard.length()) {
            throw new IllegalArgumentException(GameConstants.PLAYER_MOVE_OUT_OF_BOARD_EXCEPTION);
        }
        return newGameBoard.charAt(playerMoveDto.getGameBoardElementIndex()) != '-';
    }
    public boolean validateCurrentPlayerTurn(Game game, SimpMessageHeaderAccessor accessor) {
        Player currentPlayer = gameService.getCurrentPlayer(game);
        Player callerPlayer = gameService.retrieveAnyPlayerFromUser(accessor);
        return callerPlayer.getPawn() == currentPlayer.getPawn();
    }

    public boolean containsEmptyFields(Game game) {
       return game.getGameBoard().contains("-");
    }
}
