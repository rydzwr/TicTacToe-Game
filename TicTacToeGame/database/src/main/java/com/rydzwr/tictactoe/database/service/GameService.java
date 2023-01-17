package com.rydzwr.tictactoe.database.service;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    public void save(Game game) {
        gameRepository.save(game);
    }
}
