package com.rydzwr.tictactoe.service.security.factory;

import com.rydzwr.tictactoe.database.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleFactory {

    public Role createUserRole() {
        return new Role("USER");
    }

    public Role createAdminRole() {
        return new Role("ADMIN");
    }
}
