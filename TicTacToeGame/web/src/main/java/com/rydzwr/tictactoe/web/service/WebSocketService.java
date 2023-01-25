package com.rydzwr.tictactoe.web.service;

import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.selector.PlayerMoveStrategySelector;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.ProcessMoveStrategy;
import com.rydzwr.tictactoe.web.constants.CheckWinState;
import com.rydzwr.tictactoe.web.selector.GameStateStrategySelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate template;
    private final GameService gameService;
    private final PlayerMoveStrategySelector playerMoveStrategySelector;
    private final GameStateStrategySelector gameStateStrategySelector;

    public Player getCurrentPlayer(SimpMessageHeaderAccessor accessor) {
        Player callerPlayer = gameService.retrieveAnyPlayerFromUser(accessor);
        if (callerPlayer == null) {
            throw new IllegalArgumentException(GameConstants.PLAYER_NOT_FOUND_EXCEPTION);
        }
        return gameService.getCurrentPlayer(callerPlayer.getGame());
    }

    public Game processPlayerMove(SimpMessageHeaderAccessor accessor, PlayerMoveDto playerMoveDto, Player currentPlayer) {
        ProcessMoveStrategy processMoveStrategy = playerMoveStrategySelector.chooseStrategy(currentPlayer.getPlayerType());
        return processMoveStrategy.processPlayerMove(currentPlayer.getGame(), accessor, playerMoveDto);
    }

    public Game processAIPlayers(SimpMessageHeaderAccessor accessor, Game game, PlayerMoveDto playerMoveDto) {
        if (gameService.isNextPlayerAIType(game)) {
            do {
                var player = gameService.getCurrentPlayer(game);
                Game gameAfterAi = processPlayerMove(accessor, playerMoveDto, player);

                processGameStatus(gameAfterAi, player);

            } while (gameService.isNextPlayerAIType(game));
        }
        return game;
    }

    public void processGameStatus(Game game, Player player) {
        var gameStatus = checkWin(game);

        var strategy =  gameStateStrategySelector.chooseStrategy(gameStatus);
        strategy.send(game, player, template);
    }

    private CheckWinState checkWin(Game game) {
        if (!gameService.containsEmptyFields(game)) {
            return CheckWinState.DRAW;
        }

        if (gameService.checkWin(game)) {
            return CheckWinState.WIN;
        }
        return CheckWinState.CONTINUE;
    }
}
