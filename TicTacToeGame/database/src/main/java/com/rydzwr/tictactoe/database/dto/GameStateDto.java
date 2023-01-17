package com.rydzwr.tictactoe.database.dto;

import com.rydzwr.tictactoe.database.model.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateDto {
    private String gameState;
}
