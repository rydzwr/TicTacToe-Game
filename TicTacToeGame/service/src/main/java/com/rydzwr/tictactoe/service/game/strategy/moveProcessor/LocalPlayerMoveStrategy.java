package com.rydzwr.tictactoe.service.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
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
public class LocalPlayerMoveStrategy implements ProcessMoveStrategy{
    private final PlayerMoveValidator playerMoveValidator;
    private final GameDatabaseService gameDatabaseService;

    @Override
    @Transactional
    public void processPlayerMove(PlayerMoveResponseDto moves, GameAdapter gameAdapter, SimpMessageHeaderAccessor accessor, MoveCoordsDto moveCoordsDto) {

        // IF PLAYER PRESSED OCCUPIED FIELD
        if (playerMoveValidator.validatePlayerMove(gameAdapter, moveCoordsDto)) {
            throw new IllegalArgumentException(GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION);
        }

        char playerPawn = gameAdapter.getCurrentPlayer().getPawn();
        gameAdapter.updateCurrentPlayerTurn();

        moves.addValueToProcessedMovesIndices(moveCoordsDto, gameAdapter);
        moves.addValueToProcessedMovesPawns(playerPawn);

        gameAdapter.updateGameBoard(moveCoordsDto, playerPawn);
        gameDatabaseService.save(gameAdapter.getGame());
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.LOCAL);
    }
}
