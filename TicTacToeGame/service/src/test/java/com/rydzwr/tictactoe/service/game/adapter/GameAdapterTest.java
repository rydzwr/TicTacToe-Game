package com.rydzwr.tictactoe.service.game.adapter;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class GameAdapterTest {

    @Test
    public void testCountEmptyGameSlots() {
        // PLAYERS LIST
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        // GIVEN
        var game = Mockito.mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        Mockito.when(game.getPlayersCount()).thenReturn(3);
        Mockito.when(game.getPlayers()).thenReturn(players);

        // THEN
        Assertions.assertEquals(1, adapter.countEmptyGameSlots());
    }

    @Test
    public void testNotContainsEmptyFields() {
        // GIVEN
        var game = Mockito.mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        Mockito.when(game.getGameBoard()).thenReturn("QWERTY");

        // THEN
        Assertions.assertTrue(adapter.notContainsEmptyFields());
    }

    @Test
    public void testContainsEmptyFields() {
        // GIVEN
        var game = Mockito.mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        Mockito.when(game.getGameBoard()).thenReturn("------");

        // THEN
        Assertions.assertFalse(adapter.notContainsEmptyFields());
    }

    @Test
    public void testGetCurrentPlayer() {
        // PLAYERS LIST
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        // GIVEN
        var game = Mockito.mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        Mockito.when(game.getPlayers()).thenReturn(players);

        // WHEN + THEN
        Mockito.when(game.getCurrentPlayerTurn()).thenReturn(0);
        Assertions.assertEquals(players.get(0), adapter.getCurrentPlayer());

        // WHEN + THEN
        Mockito.when(game.getCurrentPlayerTurn()).thenReturn(1);
        Assertions.assertEquals(players.get(1), adapter.getCurrentPlayer());
    }

    @Test
    public void testUpdateGameBoard() {
        // GIVEN
        var game = Mockito.mock(Game.class);
        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        // WHEN
        Mockito.when(game.getGameSize()).thenReturn(3);
        Mockito.when(game.getGameBoard()).thenReturn("---------");
        gameAdapter.updateGameBoard(moveCoordsDto, 'X');

        // THEN
        Mockito.verify(game).setGameBoard("X--------");
    }

    @Test
    public void testUpdateCurrentPlayerTurn() {
        // PLAYERS LIST
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        // GIVEN
        var game = Mockito.mock(Game.class);
        var gameAdapter = new GameAdapter(game);

        // WHEN
        Mockito.when(game.getPlayers()).thenReturn(players);
        Mockito.when(game.getCurrentPlayerTurn()).thenReturn(0);
        gameAdapter.updateCurrentPlayerTurn();

        // THEN
        Mockito.verify(game).setCurrentPlayerTurn(1);
    }

    @Test
    public void getPawnAtIndexTest() {
        // GIVEN
        var game = Mockito.mock(Game.class);
        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = Mockito.mock(MoveCoordsDto.class);

        // WHEN
        Mockito.when(game.getGameBoard()).thenReturn("abcdef");
        Mockito.when(game.getGameSize()).thenReturn(6);
        Mockito.when(moveCoordsDto.getIndex(6)).thenReturn(2);

        char pawn = gameAdapter.getPawnAtIndex(moveCoordsDto);

        // THEN
        Assertions.assertEquals('c', pawn);
    }

    @Test
    public void getGameBoardCopyTest() {
        // GIVEN
        var game = Mockito.mock(Game.class);
        var gameAdapter = new GameAdapter(game);

        // WHEN
        Mockito.when(game.getGameBoard()).thenReturn("qwerty");
        String boardCopy = gameAdapter.getGameBoardCopy();

        // THEN
        Assertions.assertEquals("qwerty", boardCopy);
    }
    @Test
    public void isNextPlayerAIType_whenCurrentPlayerIsAI_returnsTrue() {
        // GIVEN
        List<Player> players = new ArrayList<>();
        var game = Mockito.mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        var aiPlayer = Mockito.mock(Player.class);
        Mockito.when(aiPlayer.getPlayerType()).thenReturn(PlayerType.AI);

        // WHEN
        players.add(aiPlayer);
        Mockito.when(game.getPlayers()).thenReturn(players);
        Mockito.when(game.getCurrentPlayerTurn()).thenReturn(0);

        // TO TEST
        boolean result = adapter.isNextPlayerAIType();

        // THEN
        Assertions.assertTrue(result);
    }

    @Test
    public void isNextPlayerAIType_whenCurrentPlayerIsNotAI_returnsFalse() {
        // GIVEN
        List<Player> players = new ArrayList<>();
        var game = Mockito.mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        var aiPlayer = Mockito.mock(Player.class);
        Mockito.when(aiPlayer.getPlayerType()).thenReturn(PlayerType.ONLINE);

        // WHEN
        players.add(aiPlayer);
        Mockito.when(game.getPlayers()).thenReturn(players);
        Mockito.when(game.getCurrentPlayerTurn()).thenReturn(0);

        // TO TEST
        boolean result = adapter.isNextPlayerAIType();

        // THEN
        Assertions.assertFalse(result);
    }

    @Test
    public void isOutOfBoard_whenMoveCoordsAreOutOfBoard_returnsFalse() {
        // GIVEN
        var game = Mockito.mock(Game.class);
        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto(5, 5);

        // WHEN
        Mockito.when(game.getGameSize()).thenReturn(3);

        // TO TEST
        boolean result = gameAdapter.isOutOfBoard(moveCoordsDto);

        // THEN
        Assertions.assertFalse(result);
    }

    @Test
    public void isOutOfBoard_whenMoveCoordsAreInBoard_returnsTrue() {
        // GIVEN
        var game = Mockito.mock(Game.class);
        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto(1, 1);

        // WHEN
        Mockito.when(game.getGameSize()).thenReturn(3);

        // TO TEST
        boolean result = gameAdapter.isOutOfBoard(moveCoordsDto);

        // THEN
        Assertions.assertTrue(result);
    }

    @Test
    public void testHasPawn() {
        // GIVEN
        var moveCoordsDto = new MoveCoordsDto(0, 0);
        var game = Mockito.mock(Game.class);
        var adapter = new GameAdapter(game);

        // WHEN
        Mockito.when(game.getGameBoard()).thenReturn("XXO");
        Mockito.when(game.getGameSize()).thenReturn(3);

        // WHEN + THEN
        Assertions.assertTrue(adapter.hasPawn(moveCoordsDto, 'X'));
        Assertions.assertFalse(adapter.hasPawn(moveCoordsDto, 'O'));

        // WHEN + THEN
        moveCoordsDto = new MoveCoordsDto(0, 1);
        Assertions.assertTrue(adapter.hasPawn(moveCoordsDto, 'X'));
        Assertions.assertFalse(adapter.hasPawn(moveCoordsDto, 'O'));

        // WHEN + THEN
        moveCoordsDto = new MoveCoordsDto(0, 2);
        Assertions.assertFalse(adapter.hasPawn(moveCoordsDto, 'X'));
        Assertions.assertTrue(adapter.hasPawn(moveCoordsDto, 'O'));
    }

}
