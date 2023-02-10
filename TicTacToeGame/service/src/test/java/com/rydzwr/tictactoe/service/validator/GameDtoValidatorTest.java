package com.rydzwr.tictactoe.service.validator;

import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.validator.GameDtoValidator;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class GameDtoValidatorTest {

    private GameDtoValidator validator = new GameDtoValidator();

    @Test
    public void testValidateReceivedData_gameDifficultyLessThanGameSize() {
        var playerDto = new PlayerDto();

        var gameDto = new GameDto(5, 3, asList(playerDto, playerDto, playerDto));
        boolean result = validator.validateReceivedData(gameDto);
        assertTrue(result);
    }

    @Test
    public void testValidateReceivedData_gameDifficultyGreaterThanGameSize() {
        var playerDto = new PlayerDto();

        var gameDto = new GameDto(3, 5, asList(playerDto, playerDto, playerDto));
        boolean result = validator.validateReceivedData(gameDto);
        assertFalse(result);
    }

    @Test
    public void testValidateReceivedData_playersCountGreaterThanGameDifficulty() {
        var playerDto = new PlayerDto();

        var gameDto = new GameDto(5, 3, asList(playerDto, playerDto, playerDto, playerDto));
        boolean result = validator.validateReceivedData(gameDto);
        assertFalse(result);
    }

    @Test
    public void testValidateReceivedData_playersCountLessThanOrEqualToGameDifficulty() {
        var playerDto = new PlayerDto();

        var gameDto = new GameDto(5, 3, asList(playerDto, playerDto, playerDto));
        boolean result = validator.validateReceivedData(gameDto);
        assertTrue(result);
    }
}

