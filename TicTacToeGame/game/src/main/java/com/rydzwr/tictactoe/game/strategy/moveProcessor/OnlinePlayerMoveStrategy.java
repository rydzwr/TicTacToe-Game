package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.database.model.Game;
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
public class OnlinePlayerMoveStrategy implements ProcessMoveStrategy{
    private final GameService gameService;
    private final PlayerMoveService playerMoveService;
    private final PlayerMoveValidator playerMoveValidator;
    @Override
    @Transactional
    public void processPlayerMove(PlayerMoveResponseDto moves, Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto) {

        // NEED TO VALIDATE IF IT'S CALLER TURN
        if (!playerMoveValidator.validateCurrentPlayerTurn(game, accessor)) {
            throw new IllegalArgumentException(GameConstants.NOT_CALLER_TURN_EXCEPTION);
        }

        // IF PLAYER PRESSED OCCUPIED FIELD
        if (playerMoveValidator.validatePlayerMove(game.getGameBoard(), playerMoveDto)) {
            throw new IllegalArgumentException(GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION);
        }

        char playerPawn = gameService.getCurrentPlayer(game).getPawn();
        playerMoveService.updateCurrentPlayerTurn(game);

        moves.getProcessedMovesIndices().add(playerMoveDto.getGameBoardElementIndex());
        moves.getProcessedMovesPawns().add(playerPawn);

        playerMoveService.updateGameBoard(game, playerMoveDto, playerPawn);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.ONLINE);
    }
}
