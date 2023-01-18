package com.rydzwr.tictactoe.database.validator.game;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PlayersCountValidator.class)
public @interface ValidPlayersCount {
    String message() default "Not Enough Players";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
