package com.rydzwr.tictactoe.game.service;

import com.rydzwr.tictactoe.database.dto.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.database.service.PlayerDatabaseService;
import com.rydzwr.tictactoe.database.service.UserDatabaseService;
import com.rydzwr.tictactoe.game.selector.GameStrategySelector;
import com.rydzwr.tictactoe.game.strategy.BuildGameStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameStrategySelector selector;
    private final GameDatabaseService gameDatabaseService;
    private final UserDatabaseService userDatabaseService;
    private final PlayerDatabaseService playerDatabaseService;
    private final CheckWinAlgorithm checkWinAlgorithm;
    public void buildGame(GameDto gameDto) {
        BuildGameStrategy strategy = selector.chooseStrategy(gameDto);
        strategy.buildGame(gameDto);
    }

    @Transactional
    public Game processPlayerMove(Game game, PlayerMoveDto playerMoveDto, SimpMessagingTemplate template) {
        String newGameBoard = game.getGameBoard();

        // IF PLAYER PRESSED OCCUPIED FIELD RETURNING SAME BOARD
        if (newGameBoard.charAt(playerMoveDto.getGameBoardElementIndex()) != '-') {
            template.convertAndSend("/topic/gameBoard", new GameBoardDto(game.getGameBoard()));
            return game;
        }

        List<Player> players = game.getPlayers();

        // GETTING CURRENT PLAYER PAWN TO UPDATE GAME BOARD IN DATABASE
        int currentPlayerTurn = game.getCurrentPlayerTurn();
        char playerPawn = players.get(currentPlayerTurn).getPawn();

        // UPDATING PLAYER TURN IN DATABASE USING LOOP OF IT'S PLAYERS
        int nextPlayerTurn = updateCurrentPlayerTurn(players, currentPlayerTurn);
        game.setCurrentPlayerTurn(nextPlayerTurn);

        // BUILDING UPDATED GAME BOARD
        StringBuilder stringBuilder = new StringBuilder(newGameBoard);
        stringBuilder.setCharAt(playerMoveDto.getGameBoardElementIndex(), playerPawn);
        game.setGameBoard(stringBuilder.toString());

        // SAVING
        gameDatabaseService.save(game);
        return game;
    }

    public boolean checkWin(Game game) {
        return checkWinAlgorithm.checkWin(game);
    }

    public char getWonPawn(Game game) {
        // TODO NOT WORKING PROPERLY !!!!!!!!!!!!!!!!!
        int playerIndex = game.getCurrentPlayerTurn();
        Player player = game.getPlayers().get(playerIndex == 0 ? playerIndex : playerIndex -1);
        return player.getPawn();
    }

    public boolean isUserInGame() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User caller = userDatabaseService.findByName(userName);
        return playerDatabaseService.existsByUser(caller);
    }

    private int updateCurrentPlayerTurn(List<Player> players, int currentPlayerTurn) {
        return currentPlayerTurn == players.size() - 1 ? 0 : currentPlayerTurn + 1;
    }
}
