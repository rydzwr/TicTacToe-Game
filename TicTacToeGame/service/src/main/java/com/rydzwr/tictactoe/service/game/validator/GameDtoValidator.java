package com.rydzwr.tictactoe.service.game.validator;

import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
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
        return playersCount <= gameDifficulty;
    }
}
