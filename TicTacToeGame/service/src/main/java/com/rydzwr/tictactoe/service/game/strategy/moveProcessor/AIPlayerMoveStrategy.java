package com.rydzwr.tictactoe.service.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.game.algorithm.MinimaxAlgorithm;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.service.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import com.rydzwr.tictactoe.service.game.validator.PlayerMoveValidator;
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
    public void processPlayerMove(PlayerMoveResponseDto moves, Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto) {

        if (new GameAdapter(game).notContainsEmptyFields()) {
            throw new IllegalArgumentException(GameConstants.ALL_FIELDS_ON_BOARD_OCCUPIED_EXCEPTION);
        }

        PlayerMoveDto minimaxMove = new PlayerMoveDto();
        char playerPawn = new GameAdapter(game).getCurrentPlayer().getPawn();
        int gameBoardIndex = minimaxAlgorithm.processMove(game.getGameBoard(), playerPawn);
        minimaxMove.setGameBoardElementIndex(gameBoardIndex);

        moves.getProcessedMovesIndices().add(gameBoardIndex);
        moves.getProcessedMovesPawns().add(playerPawn);

        new GameAdapter(game).updateCurrentPlayerTurn();
        new GameAdapter(game).updateGameBoard(minimaxMove, playerPawn);
        gameDatabaseService.save(game);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.AI);
    }
}
