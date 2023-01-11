package com.rydzwr.tictactoe.database.model;

import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "players")
public class Player{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private char pawn;
    private int score;
    private String name;
    @OneToOne
    private User user;
    @ManyToOne
    private Game game;

    public Player(PlayerBuilder playerBuilder) {
        this.pawn = playerBuilder.getPawn();
        this.score = playerBuilder.getScore();
        this.name = playerBuilder.getName();
        this.user = playerBuilder.getUser();
        this.game = playerBuilder.getGame();
    }
}
