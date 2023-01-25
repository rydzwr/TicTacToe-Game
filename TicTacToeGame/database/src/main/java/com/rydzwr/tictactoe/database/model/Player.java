package com.rydzwr.tictactoe.database.model;

import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private char pawn;
    @OneToOne
    private User user;

    private int playerGameIndex;
    @ManyToOne
    private Game game;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PlayerType playerType;

    public Player(PlayerBuilder playerBuilder) {
        this.pawn = playerBuilder.getPawn();
        this.game = playerBuilder.getGame();
        this.playerType = playerBuilder.getPlayerType();
        this.playerGameIndex = playerBuilder.getPlayerGameIndex();

        if (playerBuilder.getUser() != null) {
            this.user = playerBuilder.getUser();
        }
    }
}
