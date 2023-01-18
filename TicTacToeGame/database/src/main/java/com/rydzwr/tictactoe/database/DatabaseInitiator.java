package com.rydzwr.tictactoe.database;

import com.rydzwr.tictactoe.database.factory.RoleFactory;
import com.rydzwr.tictactoe.database.factory.UserFactory;
import com.rydzwr.tictactoe.database.model.Role;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.service.RoleDatabaseService;
import com.rydzwr.tictactoe.database.service.UserDatabaseService;
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

    private final UserDatabaseService userDatabaseService;
    private final RoleDatabaseService roleDatabaseService;

    private final UserFactory userFactory;
    private final RoleFactory roleFactory;

    @Override
    @Profile("dev")
    public void run(ApplicationArguments args) {
        roleDatabaseService.deleteAll();
        userDatabaseService.deleteAll();

        Role userRole = roleFactory.createUserRole();
        Role adminRole = roleFactory.createAdminRole();
        roleDatabaseService.saveRole(userRole);
        roleDatabaseService.saveRole(adminRole);

        User user = userFactory.createUser("user", "user123");
        userDatabaseService.saveUser(user);
    }
}
