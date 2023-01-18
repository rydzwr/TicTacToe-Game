package com.rydzwr.tictactoe.game.validator;

import com.rydzwr.tictactoe.database.dto.GameDto;
import org.springframework.stereotype.Component;

@Component
public class GameDtoValidator {

    public boolean validateReceivedData(GameDto gameDto) {
        final int gameSize = gameDto.getGameSize();
        final int gameDifficulty = gameDto.getGameDifficulty();
        final int playersCount = gameDto.getPlayers().size();

        return gameDifficulty <= gameSize && playersCount <= gameDifficulty;
    }
}
