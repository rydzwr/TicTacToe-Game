package com.rydzwr.tictactoe.database.service;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public void save(Player player) {
        playerRepository.save(player);
    }
}
