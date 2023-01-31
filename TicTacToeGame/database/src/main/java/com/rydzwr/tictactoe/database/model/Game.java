package com.rydzwr.tictactoe.database.model;

import com.rydzwr.tictactoe.database.constants.GameState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private int gameSize;
    @NotNull
    private int difficulty;

    @NotNull
    private int currentPlayerTurn = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GameState state = GameState.AWAITING_PLAYERS;
    @Column(columnDefinition = "TEXT")
    private String gameBoard;

    @OrderBy("playerGameIndex")
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Player> players;

    private String inviteCode = null;

    private int playersCount;
}
