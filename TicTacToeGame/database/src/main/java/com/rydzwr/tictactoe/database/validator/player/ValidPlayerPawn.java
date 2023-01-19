package com.rydzwr.tictactoe.database.validator.player;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PlayerPawnValidator.class)
public @interface ValidPlayerPawn {
    String message() default "Invalid Player Pawn";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
