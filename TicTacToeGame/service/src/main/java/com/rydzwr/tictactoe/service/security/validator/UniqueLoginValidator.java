package com.rydzwr.tictactoe.service.security.validator;

import com.rydzwr.tictactoe.database.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, String> {
    private final UserRepository userRepository;

    public boolean isValid(String login, ConstraintValidatorContext context) {
        return login != null && !userRepository.existsByName(login);
    }
}
