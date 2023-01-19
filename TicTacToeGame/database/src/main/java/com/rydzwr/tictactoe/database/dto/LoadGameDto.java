package com.rydzwr.tictactoe.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoadGameDto {
    private String gameState;
    private char currentPlayerMove;
    private int gameSize;
}
