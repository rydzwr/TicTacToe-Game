package com.rydzwr.tictactoe.database.dto.outgoing;

import lombok.Data;

@Data
public class GameBoardDto {
    private String gameBoard;
    private char currentPlayerMove;

    public GameBoardDto(String gameBoard, char currentPlayerMove) {
        this.gameBoard = gameBoard;
        this.currentPlayerMove = currentPlayerMove;
    }
}
