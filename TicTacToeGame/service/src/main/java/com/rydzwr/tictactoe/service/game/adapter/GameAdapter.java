package com.rydzwr.tictactoe.service.game.adapter;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GameAdapter {
    private Game game;

    public int countEmptyGameSlots() {
        int gamePlayerCount = game.getPlayersCount();
        int occupiedSlotsCount = game.getPlayers().size();
        return gamePlayerCount - occupiedSlotsCount;
    }

    public boolean notContainsEmptyFields() {
        return !game.getGameBoard().contains("-");
    }

    public Player getCurrentPlayer() {
        return game.getPlayers().get(game.getCurrentPlayerTurn());
    }


    public void updateGameBoard(MoveCoordsDto moveCoordsDto, char playerPawn) {
        var moveIndex = moveCoordsDto.getIndex(game.getGameSize());

        StringBuilder stringBuilder = new StringBuilder(game.getGameBoard());
        stringBuilder.setCharAt(moveIndex, playerPawn);

        game.setGameBoard(stringBuilder.toString());
    }

    public void updateCurrentPlayerTurn() {
        List<Player> players = game.getPlayers();

        int currentPlayerTurn = game.getCurrentPlayerTurn();
        int nextPlayerTurn = currentPlayerTurn == players.size() - 1 ? 0 : currentPlayerTurn + 1;

        game.setCurrentPlayerTurn(nextPlayerTurn);
    }

    public char getPawnAtIndex(MoveCoordsDto moveCoordsDto) {
        var index = moveCoordsDto.getIndex(game.getGameSize());
        return getGameBoardCopy().charAt(index);
    }

    public char getPawnAtIndex(int index) {
        return getGameBoardCopy().charAt(index);
    }

    //--------------------------------------------------
    private char getPawnAt(MoveCoordsDto moveCoordsDto) {
        var index = moveCoordsDto.getIndex(game.getGameSize());
        char[] boardArray = game.getGameBoard().toCharArray();
        return boardArray[index];
    }
    //--------------------------------------------------

    public String getGameBoardCopy() {
        var original = game.getGameBoard();
        return original.substring(0, original.length());
    }

    public int getGameSize(){
        return game.getGameSize();
    }

    public int getDifficulty() {
        return game.getDifficulty();
    }

    public Game getGame(){
        return game;
    }
}
