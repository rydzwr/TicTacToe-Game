package com.rydzwr.tictactoe.service.dto.outgoing;

import com.rydzwr.tictactoe.database.model.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadGameDto {
    private String state;
    private String board;
    private char currentPlayerMove;
    private int difficulty;
    private int size;
    private char playerPawn;
    private Integer awaitingPlayers;

    public LoadGameDto(Game game, char playerPawn, char currentPlayerMove) {
        this.state = game.getState().name();
        this.board = game.getGameBoard();
        this.currentPlayerMove = currentPlayerMove;
        this.difficulty = game.getDifficulty();
        this.size = game.getGameSize();
        this.playerPawn = playerPawn;
        this.awaitingPlayers = null;
    }

    public LoadGameDto(Game game, char playerPawn, char currentPlayerMove, Integer awaitingPlayers) {
        this(game, playerPawn, currentPlayerMove);
        this.awaitingPlayers = awaitingPlayers;
    }
}
