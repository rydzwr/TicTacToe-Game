package com.rydzwr.tictactoe.database.dto.incoming;

import com.rydzwr.tictactoe.database.validator.game.ValidPlayersCount;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    @Min(3)
    @NotNull
    private int gameSize;
    @Min(3)
    @NotNull
    private int gameDifficulty;
    @NotNull
    @ValidPlayersCount
    private List<PlayerDto> players;
}
