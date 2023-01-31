package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.game.algorithm.MinimaxAlgorithm;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.service.PlayerMoveService;
import com.rydzwr.tictactoe.game.validator.PlayerMoveValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void processPlayerMove(PlayerMoveResponseDto moves, Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto) {

        if (!playerMoveValidator.containsEmptyFields(game)) {
            throw new IllegalArgumentException(GameConstants.ALL_FIELDS_ON_BOARD_OCCUPIED_EXCEPTION);
        }

        PlayerMoveDto minimaxMove = new PlayerMoveDto();
        char playerPawn = gameService.getCurrentPlayer(game).getPawn();
        int gameBoardIndex = minimaxAlgorithm.processMove(game.getGameBoard(), playerPawn);
        minimaxMove.setGameBoardElementIndex(gameBoardIndex);

        moves.getProcessedMovesIndices().add(gameBoardIndex);
        moves.getProcessedMovesPawns().add(playerPawn);

        playerMoveService.updateCurrentPlayerTurn(game);
        playerMoveService.updateGameBoard(game, minimaxMove, playerPawn);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.AI);
    }
}
