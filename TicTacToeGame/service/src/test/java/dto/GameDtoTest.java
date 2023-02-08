package dto;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameDtoTest {
    @Test
    public void countAIPlayers_ReturnsCorrectCount() {
        // given
        var gameDto = new GameDto();
        List<PlayerDto> players = new ArrayList<>();
        players.add(new PlayerDto(PlayerType.AI.name()));
        players.add(new PlayerDto(PlayerType.AI.name()));
        players.add(new PlayerDto(PlayerType.LOCAL.name()));
        gameDto.setPlayers(players);

        // when
        int aiPlayersCount = gameDto.countAIPlayers();

        // then
        assertEquals(2, aiPlayersCount);
    }

    @Test
    public void getHumanGameSlots_ReturnsCorrectCount() {
        // given
        GameDto gameDto = new GameDto();
        List<PlayerDto> players = new ArrayList<>();
        players.add(new PlayerDto(PlayerType.AI.name()));
        players.add(new PlayerDto(PlayerType.AI.name()));
        players.add(new PlayerDto(PlayerType.ONLINE.name()));
        gameDto.setPlayers(players);

        // when
        int humanGameSlots = gameDto.getHumanGameSlots();

        // then
        assertEquals(1, humanGameSlots);
    }
}
