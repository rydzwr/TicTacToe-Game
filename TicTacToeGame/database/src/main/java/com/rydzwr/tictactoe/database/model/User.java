package com.rydzwr.tictactoe.database.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "name", columnNames = "name") })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String password;
    private String refreshToken;
    @ManyToOne
    private Role role;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
