package com.rydzwr.tictactoe.database.service;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository repository;


    @Transactional
    public void save(Game game) {
        repository.save(game);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}
