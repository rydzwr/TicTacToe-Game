package com.rydzwr.tictactoe.service.security.factory;

import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.service.security.database.RoleDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFactory {
    private final PasswordEncoder passwordEncoder;
    private final RoleDatabaseService roleDatabaseService;

    public User createUser(String name, String password) {
        password = passwordEncoder.encode(password);

        User user = new User(name, password);
        user.setRole(roleDatabaseService.findByName("USER"));
        return user;
    }
}
