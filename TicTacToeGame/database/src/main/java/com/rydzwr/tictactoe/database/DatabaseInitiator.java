package com.rydzwr.tictactoe.database;

import com.rydzwr.tictactoe.database.factory.RoleFactory;
import com.rydzwr.tictactoe.database.factory.UserFactory;
import com.rydzwr.tictactoe.database.model.Role;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.service.RoleService;
import com.rydzwr.tictactoe.database.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitiator implements ApplicationRunner {

    private final UserService userService;
    private final RoleService roleService;

    private final UserFactory userFactory;
    private final RoleFactory roleFactory;

    @Override
    @Profile("dev")
    public void run(ApplicationArguments args) {
        roleService.deleteAll();
        userService.deleteAll();

        Role userRole = roleFactory.createUserRole();
        Role adminRole = roleFactory.createAdminRole();
        roleService.saveRole(userRole);
        roleService.saveRole(adminRole);

        User user = userFactory.createUser("user", "user123");
        userService.saveUser(user);
    }
}
