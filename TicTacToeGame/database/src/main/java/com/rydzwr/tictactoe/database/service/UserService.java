package com.rydzwr.tictactoe.database.service;

import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User findByName(String name) {
        return repository.findByName(name);
    }

    public User findByRefreshToken(String refreshToken) {
        return repository.findByRefreshToken(refreshToken);
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Transactional
    public void save(User user) {
        repository.save(user);
    }
}
