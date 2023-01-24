package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.game.algorithm.MinimaxAlgorithm;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.service.PlayerMoveService;
import com.rydzwr.tictactoe.game.validator.PlayerMoveValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIPlayerMoveStrategy implements ProcessMoveStrategy{
    private final GameService gameService;
    private final PlayerMoveService playerMoveService;
    private final PlayerMoveValidator playerMoveValidator;
    private final MinimaxAlgorithm minimaxAlgorithm;
    @Override
    public Game processPlayerMove(Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto) {

        if (!playerMoveValidator.containsEmptyFields(game)) {
            throw new IllegalArgumentException(GameConstants.ALL_FIELDS_ON_BOARD_OCCUPIED_EXCEPTION);
        }

        PlayerMoveDto minimaxMove = new PlayerMoveDto();
        char playerPawn = gameService.getCurrentPlayer(game).getPawn();
        int gameBoardIndex = minimaxAlgorithm.processMove(game.getGameBoard(), playerPawn);
        minimaxMove.setGameBoardElementIndex(gameBoardIndex);

        game = playerMoveService.updateCurrentPlayerTurn(game);
        return playerMoveService.updateGameBoard(game, minimaxMove, playerPawn);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.AI);
    }
}
