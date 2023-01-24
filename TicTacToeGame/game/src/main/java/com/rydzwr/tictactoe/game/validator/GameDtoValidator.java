package com.rydzwr.tictactoe.game.validator;

import com.rydzwr.tictactoe.database.dto.incoming.GameDto;
import org.springframework.stereotype.Component;

@Component
public class GameDtoValidator {

    public boolean validateReceivedData(GameDto gameDto) {
        final int gameSize = gameDto.getGameSize();
        final int gameDifficulty = gameDto.getGameDifficulty();
        final int playersCount = gameDto.getPlayers().size();

        if (gameDifficulty > gameSize) {
            return false;
        }
        if (playersCount > gameDifficulty) {
            return false;
        }

        return true;
    }
}
