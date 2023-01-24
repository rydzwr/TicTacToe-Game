package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
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
public class LocalPlayerMoveStrategy implements ProcessMoveStrategy{
    private final GameService gameService;
    private final PlayerMoveValidator playerMoveValidator;
    private final PlayerMoveService playerMoveService;

    @Override
    public Game processPlayerMove(Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto) {

        // IF PLAYER PRESSED OCCUPIED FIELD
        if (playerMoveValidator.validatePlayerMove(game.getGameBoard(), playerMoveDto)) {
            throw new IllegalArgumentException(GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION);
        }

        char playerPawn = gameService.getCurrentPlayer(game).getPawn();
        game = playerMoveService.updateCurrentPlayerTurn(game);
        return playerMoveService.updateGameBoard(game, playerMoveDto, playerPawn);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.LOCAL);
    }
}
