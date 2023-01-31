package com.rydzwr.tictactoe.service.dto.incoming;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.game.validator.game.ValidPlayersCount;
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

    public int countAIPlayers() {
        return (int) this.getPlayers().stream()
                .filter((playerDto -> playerDto.getPlayerType().equals(PlayerType.AI.name())))
                .count();
    }

    public int getHumanGameSlots() {
        int allGameSlots = this.getPlayers().size();

        int aIPlayersCount = (int) this.getPlayers().stream()
                .filter((playerDto -> playerDto.getPlayerType().equals(PlayerType.AI.name())))
                .count();

        return allGameSlots - aIPlayersCount;
    }
}
