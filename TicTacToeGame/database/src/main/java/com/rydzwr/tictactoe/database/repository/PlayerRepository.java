package com.rydzwr.tictactoe.database.repository;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {
    boolean existsByUser(User user);
    Player findFirstByUser(User user);
    List<Player> findAllByGame(Game game);
}
