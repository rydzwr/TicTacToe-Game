package com.rydzwr.tictactoe.database.validator.player;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class PlayerPawnValidator implements ConstraintValidator<ValidPlayerPawn, Character> {
    String validPlayerPawns = "abcdefghijklmnoprstuwxyz";
    List<Character> validChars = new ArrayList<>();
    char[] chars = validPlayerPawns.toCharArray();

    private void init() {
        for (char aChar : chars) {
            validChars.add(aChar);
        }
    }

    @Override
    public boolean isValid(Character character, ConstraintValidatorContext constraintValidatorContext) {
        init();
        return validChars.contains(character);
    }
}
