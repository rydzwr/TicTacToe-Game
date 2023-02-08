package adapter;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameAdapterTest {

    @Test
    public void testCountEmptyGameSlots() {
        // PLAYERS LIST
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        // GIVEN
        var game = mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        when(game.getPlayersCount()).thenReturn(3);
        when(game.getPlayers()).thenReturn(players);

        // THEN
        assertEquals(1, adapter.countEmptyGameSlots());
    }

    @Test
    public void testNotContainsEmptyFields() {
        // GIVEN
        var game = mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        when(game.getGameBoard()).thenReturn("QWERTY");

        // THEN
        assertTrue(adapter.notContainsEmptyFields());
    }

    @Test
    public void testContainsEmptyFields() {
        // GIVEN
        var game = mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        when(game.getGameBoard()).thenReturn("------");

        // THEN
        assertFalse(adapter.notContainsEmptyFields());
    }

    @Test
    public void testGetCurrentPlayer() {
        // PLAYERS LIST
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        // GIVEN
        var game = mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        when(game.getPlayers()).thenReturn(players);

        // WHEN + THEN
        when(game.getCurrentPlayerTurn()).thenReturn(0);
        assertEquals(players.get(0), adapter.getCurrentPlayer());

        // WHEN + THEN
        when(game.getCurrentPlayerTurn()).thenReturn(1);
        assertEquals(players.get(1), adapter.getCurrentPlayer());
    }

    @Test
    public void testUpdateGameBoard() {
        // GIVEN
        var game = mock(Game.class);
        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        // WHEN
        when(game.getGameSize()).thenReturn(3);
        when(game.getGameBoard()).thenReturn("---------");
        gameAdapter.updateGameBoard(moveCoordsDto, 'X');

        // THEN
        verify(game).setGameBoard("X--------");
    }

    @Test
    public void testUpdateCurrentPlayerTurn() {
        // PLAYERS LIST
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        // GIVEN
        var game = mock(Game.class);
        var gameAdapter = new GameAdapter(game);

        // WHEN
        when(game.getPlayers()).thenReturn(players);
        when(game.getCurrentPlayerTurn()).thenReturn(0);
        gameAdapter.updateCurrentPlayerTurn();

        // THEN
        verify(game).setCurrentPlayerTurn(1);
    }

    @Test
    public void getPawnAtIndexTest() {
        // GIVEN
        var game = mock(Game.class);
        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = mock(MoveCoordsDto.class);

        // WHEN
        when(game.getGameBoard()).thenReturn("abcdef");
        when(game.getGameSize()).thenReturn(6);
        when(moveCoordsDto.getIndex(6)).thenReturn(2);

        char pawn = gameAdapter.getPawnAtIndex(moveCoordsDto);

        // THEN
        assertEquals('c', pawn);
    }

    @Test
    public void getGameBoardCopyTest() {
        // GIVEN
        var game = mock(Game.class);
        var gameAdapter = new GameAdapter(game);

        // WHEN
        when(game.getGameBoard()).thenReturn("qwerty");
        String boardCopy = gameAdapter.getGameBoardCopy();

        // THEN
        assertEquals("qwerty", boardCopy);
    }
    @Test
    public void isNextPlayerAIType_whenCurrentPlayerIsAI_returnsTrue() {
        // GIVEN
        List<Player> players = new ArrayList<>();
        var game = mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        var aiPlayer = mock(Player.class);
        when(aiPlayer.getPlayerType()).thenReturn(PlayerType.AI);

        // WHEN
        players.add(aiPlayer);
        when(game.getPlayers()).thenReturn(players);
        when(game.getCurrentPlayerTurn()).thenReturn(0);

        // TO TEST
        boolean result = adapter.isNextPlayerAIType();

        // THEN
        assertTrue(result);
    }

    @Test
    public void isNextPlayerAIType_whenCurrentPlayerIsNotAI_returnsFalse() {
        // GIVEN
        List<Player> players = new ArrayList<>();
        var game = mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        var aiPlayer = mock(Player.class);
        when(aiPlayer.getPlayerType()).thenReturn(PlayerType.ONLINE);

        // WHEN
        players.add(aiPlayer);
        when(game.getPlayers()).thenReturn(players);
        when(game.getCurrentPlayerTurn()).thenReturn(0);

        // TO TEST
        boolean result = adapter.isNextPlayerAIType();

        // THEN
        assertFalse(result);
    }

    @Test
    public void isOutOfBoard_whenMoveCoordsAreOutOfBoard_returnsFalse() {
        // GIVEN
        var game = mock(Game.class);
        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto(5, 5);

        // WHEN
        when(game.getGameSize()).thenReturn(3);

        // TO TEST
        boolean result = gameAdapter.isOutOfBoard(moveCoordsDto);

        // THEN
        assertFalse(result);
    }

    @Test
    public void isOutOfBoard_whenMoveCoordsAreInBoard_returnsTrue() {
        // GIVEN
        var game = mock(Game.class);
        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto(1, 1);

        // WHEN
        when(game.getGameSize()).thenReturn(3);

        // TO TEST
        boolean result = gameAdapter.isOutOfBoard(moveCoordsDto);

        // THEN
        assertTrue(result);
    }

    @Test
    public void testHasPawn() {
        // GIVEN
        var moveCoordsDto = new MoveCoordsDto(0, 0);
        var game = mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        when(game.getGameBoard()).thenReturn("XXO");
        when(game.getGameSize()).thenReturn(3);

        // WHEN + THEN
        assertTrue(adapter.hasPawn(moveCoordsDto, 'X'));
        assertFalse(adapter.hasPawn(moveCoordsDto, 'O'));

        // WHEN + THEN
        moveCoordsDto = new MoveCoordsDto(0, 1);
        assertTrue(adapter.hasPawn(moveCoordsDto, 'X'));
        assertFalse(adapter.hasPawn(moveCoordsDto, 'O'));

        // WHEN + THEN
        moveCoordsDto = new MoveCoordsDto(0, 2);
        assertFalse(adapter.hasPawn(moveCoordsDto, 'X'));
        assertTrue(adapter.hasPawn(moveCoordsDto, 'O'));
    }

}
