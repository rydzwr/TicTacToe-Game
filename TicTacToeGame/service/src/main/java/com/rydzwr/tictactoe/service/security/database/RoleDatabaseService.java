package com.rydzwr.tictactoe.service.security.database;

import com.rydzwr.tictactoe.database.model.Role;
import com.rydzwr.tictactoe.database.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleDatabaseService {
    private final RoleRepository repository;

    public Role findByName(String name) {
        return repository.findByName(name);
    }

    @Transactional
    public void saveRole(Role role) {
        repository.save(role);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}