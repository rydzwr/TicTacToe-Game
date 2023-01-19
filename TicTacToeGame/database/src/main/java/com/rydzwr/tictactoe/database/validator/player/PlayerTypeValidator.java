package com.rydzwr.tictactoe.database.validator.player;

import com.rydzwr.tictactoe.database.constants.DatabaseConstants;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlayerTypeValidator implements ConstraintValidator<ValidPlayerType, PlayerType> {
    @Override
    public boolean isValid(PlayerType playerType, ConstraintValidatorContext constraintValidatorContext) {
        return DatabaseConstants.playerTypes.contains(playerType.name());
    }
}
