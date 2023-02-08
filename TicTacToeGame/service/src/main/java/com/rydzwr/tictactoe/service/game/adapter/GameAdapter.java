package com.rydzwr.tictactoe.service.game.adapter;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
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

    public boolean isNextPlayerAIType() {
        return getCurrentPlayer().getPlayerType().equals(PlayerType.AI);
    }

    public boolean isOutOfBoard(MoveCoordsDto moveCoordsDto) {
        int x = moveCoordsDto.getX();
        int y = moveCoordsDto.getY();
        int gameSize = getGameSize();
        return x >= 0 && y >= 0 && x < gameSize && y < gameSize;
    }

    public boolean hasPawn(MoveCoordsDto moveCoordsDto, char candidate) {
        int index = moveCoordsDto.getIndex(getGameSize());
        String gameBoard = getGameBoardCopy();
        if (index >= 0 && index < gameBoard.length()) {
            return gameBoard.charAt(index) == candidate;
        }
        return false;
    }
}
