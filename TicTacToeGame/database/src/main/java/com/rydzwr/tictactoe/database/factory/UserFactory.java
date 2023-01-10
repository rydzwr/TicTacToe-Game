package com.rydzwr.tictactoe.database.factory;

import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFactory {
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public User createUser(String name, String password) {
        password = passwordEncoder.encode(password);

        User user = new User(name, password);
        user.setRole(roleService.findByName("USER"));
        return user;
    }
}
