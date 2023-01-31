package com.rydzwr.tictactoe.game.service;

import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerMoveService {
    private final GameDatabaseService gameDatabaseService;

    public void updateGameBoard(Game game, PlayerMoveDto playerMoveDto, char playerPawn) {
        StringBuilder stringBuilder = new StringBuilder(game.getGameBoard());
        stringBuilder.setCharAt(playerMoveDto.getGameBoardElementIndex(), playerPawn);

        game.setGameBoard(stringBuilder.toString());
        gameDatabaseService.save(game);
    }

    public void updateCurrentPlayerTurn(Game game) {
        List<Player> players = game.getPlayers();

        int currentPlayerTurn = game.getCurrentPlayerTurn();
        int nextPlayerTurn = currentPlayerTurn == players.size() - 1 ? 0 : currentPlayerTurn + 1;

        game.setCurrentPlayerTurn(nextPlayerTurn);
        gameDatabaseService.save(game);
    }
}
