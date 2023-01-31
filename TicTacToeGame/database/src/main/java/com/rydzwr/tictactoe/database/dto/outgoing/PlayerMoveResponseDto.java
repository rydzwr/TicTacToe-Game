package com.rydzwr.tictactoe.database.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerMoveResponseDto {
    private List<Integer> processedMovesIndices;
    private List<Character> processedMovesPawns;
    private char currentPlayerMove;

    public PlayerMoveResponseDto() {
        this.processedMovesIndices = new ArrayList<>();
        this.processedMovesPawns = new ArrayList<>();
        this.currentPlayerMove = '-';
    }
}
