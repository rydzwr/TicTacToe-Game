package com.rydzwr.tictactoe.database.repository;

import com.rydzwr.tictactoe.database.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findByName(String name);
}