package com.rydzwr.tictactoe.game.strategy;

import com.rydzwr.tictactoe.database.constants.DatabaseConstants;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.PlayerDto;
import com.rydzwr.tictactoe.game.constants.GameConstants;

import java.util.List;

public class ErrorGameTypeStrategy implements BuildGameStrategy {
    @Override
    public void buildGame(GameDto gameDto) {
        throw new IllegalArgumentException(GameConstants.INVALID_GAME_TYPE_EXCEPTION);
    }

    @Override
    public boolean applies(GameDto gameDto) {
        List<String> playerTypes = gameDto.getPlayers().stream().map(PlayerDto::getPlayerType).toList();
        return !playerTypes.equals(DatabaseConstants.playerTypes);
    }
}
