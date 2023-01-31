package com.rydzwr.tictactoe.service.game.validator.player;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PlayerTypeValidator.class)
public @interface ValidPlayerType {
    String message() default "Invalid Player Pawn";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
