package com.rydzwr.tictactoe.game.strategy.moveProcessor;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class OnlinePlayerMoveStrategy implements ProcessMoveStrategy{
    private final GameService gameService;
    private final PlayerMoveService playerMoveService;
    private final PlayerMoveValidator playerMoveValidator;
    @Override
    @Transactional
    public Game processPlayerMove(Game game, SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto) {

        // NEED TO VALIDATE IF IT'S CALLER TURN
        if (!playerMoveValidator.validateCurrentPlayerTurn(game, accessor)) {
            log.warn("ONLINE PLAYER MOVE STRATEGY: --> " + GameConstants.NOT_CALLER_TURN_EXCEPTION);
            return game;
        }

        // IF PLAYER PRESSED OCCUPIED FIELD
        if (playerMoveValidator.validatePlayerMove(game.getGameBoard(), playerMoveDto)) {
            log.warn("ONLINE PLAYER MOVE STRATEGY: --> " + GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION);
            return game;
        }

        char playerPawn = gameService.getCurrentPlayer(game).getPawn();
        game = playerMoveService.updateCurrentPlayerTurn(game);
        return playerMoveService.updateGameBoard(game, playerMoveDto, playerPawn);
    }

    @Override
    public boolean applies(PlayerType playerType) {
        return playerType.equals(PlayerType.ONLINE);
    }
}
