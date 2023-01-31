package com.rydzwr.tictactoe.service.game.database;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameDatabaseService {
    private final GameRepository gameRepository;

    public Game findById(int id) {
        return gameRepository.findById(id).get();
    }
    public void save(Game game) {
        gameRepository.save(game);
    }

    public void delete(Game game) {
        gameRepository.delete(game);
    }

    @Transactional
    public void deleteById(int id) {
        gameRepository.deleteById(id);
    }

    public Game findByInviteCode(String inviteCode) {
        return gameRepository.findByInviteCode(inviteCode);
    }

    public boolean existsByInviteCode(String inviteCode) {
        return gameRepository.existsByInviteCode(inviteCode);
    }
}
