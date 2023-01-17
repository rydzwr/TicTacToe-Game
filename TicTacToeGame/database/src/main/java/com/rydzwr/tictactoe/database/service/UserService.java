package com.rydzwr.tictactoe.database.service;

import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public void saveUser(User appUser) {
        repository.save(appUser);
    }

    public User findByName(String name) {
        return repository.findByName(name);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
