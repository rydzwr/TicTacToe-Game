package com.rydzwr.tictactoe.database.service;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerDatabaseService {
    private final PlayerRepository playerRepository;

    public void save(Player player) {
        playerRepository.save(player);
    }

    public Player findById(int id) {
        return playerRepository.findById(id).get();
    }

    public boolean existsByUser(User user) {
        return playerRepository.existsByUser(user);
    }

    public Player findFirstByUser(User user) {
        return playerRepository.findFirstByUser(user);
    }
}
