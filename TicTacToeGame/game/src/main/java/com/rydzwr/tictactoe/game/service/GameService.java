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
    public void buildGame(GameDto gameDto) {
        BuildGameStrategy strategy = selector.chooseStrategy(gameDto);
        strategy.buildGame(gameDto);
    }

    @Transactional
    public Game processPlayerMove(Game game, PlayerMoveDto playerMoveDto, SimpMessagingTemplate template) {
        String newGameBoard = game.getGameBoard();
        if (newGameBoard.charAt(playerMoveDto.getI()) != '-') {
            template.convertAndSend("/topic/gameBoard", new GameBoardDto(game.getGameBoard()));
        }

        List<Player> players = game.getPlayers();

        int currentPlayerTurn = game.getCurrentPlayerTurn();
        char playerPawn = players.get(currentPlayerTurn).getPawn();
        game.setCurrentPlayerTurn(currentPlayerTurn == players.size() - 1 ? 0 : currentPlayerTurn + 1);

        StringBuilder stringBuilder = new StringBuilder(newGameBoard);
        stringBuilder.setCharAt(playerMoveDto.getI(), playerPawn);
        game.setGameBoard(stringBuilder.toString());
        gameDatabaseService.save(game);
        return game;
    }

    public boolean checkWin(Game game) {
        CheckWinAlgorithm checkWinAlgorithm = new CheckWinAlgorithm();
        return checkWinAlgorithm.checkWin(game);
    }

    public char getWonPawn(Game game) {
        // TODO NOT SURE IT'S GONNA WORK PROPERLY
        Player player = game.getPlayers().get(game.getCurrentPlayerTurn() - 1);
        return player.getPawn();
    }

    public boolean isUserInGame() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User caller = userDatabaseService.findByName(userName);
        return playerDatabaseService.existsByUser(caller);
    }
}
