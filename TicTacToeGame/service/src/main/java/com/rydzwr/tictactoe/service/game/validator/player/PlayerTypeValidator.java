package com.rydzwr.tictactoe.service.game.validator.player;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlayerTypeValidator implements ConstraintValidator<ValidPlayerType, PlayerType> {
    @Override
    public boolean isValid(PlayerType playerType, ConstraintValidatorContext constraintValidatorContext) {
        return GameConstants.playerTypes.contains(playerType.name());
    }
}
