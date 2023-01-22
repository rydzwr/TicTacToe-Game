package com.rydzwr.tictactoe.database.service;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameDatabaseService {
    private final GameRepository gameRepository;

    public void save(Game game) {
        gameRepository.save(game);
    }

    public void delete(Game game) {
        gameRepository.delete(game);
    }

    public Game findByInviteCode(String inviteCode) {
        return gameRepository.findByInviteCode(inviteCode);
    }

    public boolean existsByInviteCode(String inviteCode) {
        return gameRepository.existsByInviteCode(inviteCode);
    }
}
