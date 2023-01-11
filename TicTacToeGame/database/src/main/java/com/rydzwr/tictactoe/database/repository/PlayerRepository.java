package com.rydzwr.tictactoe.database.repository;

import com.rydzwr.tictactoe.database.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {
}
