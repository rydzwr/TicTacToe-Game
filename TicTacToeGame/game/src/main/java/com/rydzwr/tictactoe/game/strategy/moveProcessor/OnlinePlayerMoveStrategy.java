package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnlinePlayerMoveStrategy implements ProcessMoveStrategy{
    private final GameService gameService;
    private final GameDatabaseService gameDatabaseService;
    @Override
    public Game processPlayerMove(Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto) {
        String newGameBoard = game.getGameBoard();

        // NEED TO VALIDATE IF IT'S CALLER TURN
        if (!gameService.validateCurrentPlayerTurn(game, accessor)) {
            throw new IllegalArgumentException(GameConstants.NOT_CALLER_TURN_EXCEPTION);
        }

        // IF PLAYER PRESSED OCCUPIED FIELD
        if (gameService.validatePlayerMove(newGameBoard, playerMoveDto)) {
            throw new IllegalArgumentException(GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION);
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
        return game;
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.ONLINE);
    }
}
