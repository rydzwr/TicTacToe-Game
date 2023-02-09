package com.rydzwr.tictactoe.service.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.algorithm.MinimaxAlgorithm;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIPlayerMoveStrategy implements ProcessMoveStrategy{
    private final MinimaxAlgorithm minimaxAlgorithm;
    private final GameDatabaseService gameDatabaseService;
    @Override
    @Transactional
    public void processPlayerMove(PlayerMoveResponseDto moves, GameAdapter gameAdapter, SimpMessageHeaderAccessor accessor, MoveCoordsDto moveCoordsDto) {

        if (gameAdapter.notContainsEmptyFields()) {
            throw new IllegalArgumentException(GameConstants.ALL_FIELDS_ON_BOARD_OCCUPIED_EXCEPTION);
        }

        char playerPawn = gameAdapter.getCurrentPlayer().getPawn();
        int gameBoardIndex = minimaxAlgorithm.processMove(gameAdapter, playerPawn);

        var aiMove = new MoveCoordsDto();
        aiMove.setCoords(gameBoardIndex, gameAdapter.getGameSize());

        moves.addValueToProcessedMovesIndices(aiMove, gameAdapter);
        moves.addValueToProcessedMovesPawns(playerPawn);

        gameAdapter.updateCurrentPlayerTurn();
        gameAdapter.updateGameBoard(aiMove, playerPawn);
        gameDatabaseService.save(gameAdapter.getGame());
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.AI);
    }
}
