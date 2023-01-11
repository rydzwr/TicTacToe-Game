package com.rydzwr.tictactoe.database.service;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;

    @Transactional
    public void save(Player player) {
        repository.save(player);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}
