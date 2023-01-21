package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HumanPlayerMoveStrategy implements ProcessMoveStrategy{
    private final GameService gameService;
    private final GameDatabaseService gameDatabaseService;

    @Override
    public Game processPlayerMove(Game game, PlayerMoveDto playerMoveDto) {
        log.info("HUMAN PLAYER MOVE STRATEGY: --> Processing HUMAN Move");
        String newGameBoard = game.getGameBoard();

        // IF PLAYER PRESSED OCCUPIED FIELD RETURNING SAME BOARD
        if (!gameService.validatePlayerMove(newGameBoard, playerMoveDto)) {
            log.info("HumanPlayerMoveStrategy (processPlayerMove): --> PLAYER PRESSED OCCUPIED FIELD");
            return game;
        }

        List<Player> players = game.getPlayers();

        // GETTING CURRENT PLAYER PAWN TO UPDATE GAME BOARD IN DATABASE
        int currentPlayerTurn = game.getCurrentPlayerTurn();
        char playerPawn = players.get(currentPlayerTurn).getPawn();

        // UPDATING PLAYER TURN IN DATABASE USING LOOP OF IT'S PLAYERS
        int nextPlayerTurn = gameService.updateCurrentPlayerTurn(players, currentPlayerTurn);
        game.setCurrentPlayerTurn(nextPlayerTurn);

        // BUILDING UPDATED GAME BOARD
        StringBuilder stringBuilder = new StringBuilder(newGameBoard);
        stringBuilder.setCharAt(playerMoveDto.getGameBoardElementIndex(), playerPawn);
        game.setGameBoard(stringBuilder.toString());

        // SAVING
        gameDatabaseService.save(game);
        log.info("HUMAN PLAYER MOVE STRATEGY: --> Human Move Has Been Proceed");
        return game;
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.LOCAL);
    }
}
