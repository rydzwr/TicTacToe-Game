package com.rydzwr.tictactoe.database.repository;

import com.rydzwr.tictactoe.database.model.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, Integer> {
    Game findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);
}
