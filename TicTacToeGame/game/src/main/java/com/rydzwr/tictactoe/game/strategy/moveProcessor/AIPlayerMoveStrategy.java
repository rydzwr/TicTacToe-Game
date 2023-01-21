package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.game.algorithm.MinimaxAlgorithm;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIPlayerMoveStrategy implements ProcessMoveStrategy{
    private final GameService gameService;
    private final GameDatabaseService gameDatabaseService;

    private final MinimaxAlgorithm minimaxAlgorithm;
    @Override
    public Game processPlayerMove(Game game, PlayerMoveDto playerMoveDto) {
        log.info("AI PLAYER MOVE STRATEGY: --> Processing AI Move");
        String newGameBoard = game.getGameBoard();

        List<Player> players = game.getPlayers();

        // GETTING CURRENT PLAYER PAWN TO UPDATE GAME BOARD IN DATABASE
        int currentPlayerTurn = game.getCurrentPlayerTurn();
        char playerPawn = players.get(currentPlayerTurn).getPawn();

        PlayerMoveDto minimaxMove = new PlayerMoveDto();
        int gameBoardIndex = minimaxAlgorithm.processMove(newGameBoard, playerPawn);
        minimaxMove.setGameBoardElementIndex(gameBoardIndex);

        // UPDATING PLAYER TURN IN DATABASE USING LOOP OF IT'S PLAYERS
        int nextPlayerTurn = gameService.updateCurrentPlayerTurn(players, currentPlayerTurn);
        game.setCurrentPlayerTurn(nextPlayerTurn);

        // BUILDING UPDATED GAME BOARD
        StringBuilder stringBuilder = new StringBuilder(newGameBoard);
        stringBuilder.setCharAt(minimaxMove.getGameBoardElementIndex(), playerPawn);
        game.setGameBoard(stringBuilder.toString());

        // SAVING
        gameDatabaseService.save(game);
        log.info("AI PLAYER MOVE STRATEGY: --> Move Has Been Proceed");
        return game;
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.AI);
    }
}
