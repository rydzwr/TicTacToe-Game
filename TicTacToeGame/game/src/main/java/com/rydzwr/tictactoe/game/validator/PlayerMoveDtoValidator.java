package com.rydzwr.tictactoe.game.validator;

import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import org.springframework.stereotype.Component;

@Component
public class PlayerMoveDtoValidator {
    public boolean isValid(PlayerMoveDto playerMoveDto, Game game) {
        return playerMoveDto.getGameBoardElementIndex() <= game.getGameBoard().length();
    }
}
