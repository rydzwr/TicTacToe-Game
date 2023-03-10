package com.rydzwr.tictactoe.service.game.validator.game;

import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class PlayersCountValidator implements ConstraintValidator<ValidPlayersCount, List<PlayerDto>> {
    @Override
    public boolean isValid(List<PlayerDto> players, ConstraintValidatorContext constraintValidatorContext) {
        return players.size() >= 1;
    }
}
