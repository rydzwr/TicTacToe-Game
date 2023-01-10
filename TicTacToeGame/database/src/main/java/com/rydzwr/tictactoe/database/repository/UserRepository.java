package com.rydzwr.tictactoe.database.repository;

import com.rydzwr.tictactoe.database.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByName(String name);
    boolean existsByName(String name);
    User findByRefreshToken(String token);
}

