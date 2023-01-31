package com.rydzwr.tictactoe.service.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
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
public class LocalPlayerMoveStrategy implements ProcessMoveStrategy{
    private final PlayerMoveValidator playerMoveValidator;
    private final GameDatabaseService gameDatabaseService;

    @Override
    @Transactional
    public void processPlayerMove(PlayerMoveResponseDto moves, Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto) {

        // IF PLAYER PRESSED OCCUPIED FIELD
        if (playerMoveValidator.validatePlayerMove(game.getGameBoard(), playerMoveDto)) {
            throw new IllegalArgumentException(GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION);
        }

        char playerPawn = new GameAdapter(game).getCurrentPlayer().getPawn();
        new GameAdapter(game).updateCurrentPlayerTurn();

        moves.getProcessedMovesIndices().add(playerMoveDto.getGameBoardElementIndex());
        moves.getProcessedMovesPawns().add(playerPawn);

        new GameAdapter(game).updateGameBoard(playerMoveDto, playerPawn);
        gameDatabaseService.save(game);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.LOCAL);
    }
}
