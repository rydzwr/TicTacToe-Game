package com.rydzwr.tictactoe.database.model;

import com.rydzwr.tictactoe.database.builder.GameBuilder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@Entity
@Table(name = "games")
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotNull
    @Size(min = 3)
    private int gameSize;
    @NotNull
    @Size(min = 3)
    private int difficulty;
    private String gameBoard;

    @OneToMany(mappedBy = "game")
    private List<Player> players;

    public Game(GameBuilder gameBuilder) {
        this.gameSize = gameBuilder.getGameSize();
        this.difficulty = gameBuilder.getDifficulty();
        this.gameBoard = gameBuilder.getGameBoard();
    }
}
