package com.rydzwr.tictactoe.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerDto {
    private String playerType;
    private char playerPawn;
}
