package com.rydzwr.tictactoe.service.game;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.strategy.selector.GameStateStrategySelector;
import com.rydzwr.tictactoe.service.game.strategy.selector.PlayerMoveStrategySelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;
    private final GameService gameService;
    private final PlayerMoveStrategySelector playerMoveStrategySelector;
    private final GameStateStrategySelector gameStateStrategySelector;

    public Player getCurrentPlayer(SimpMessageHeaderAccessor accessor) {
        Player callerPlayer = gameService.retrieveAnyPlayerFromUser(accessor);
        if (callerPlayer == null) {
            throw new IllegalArgumentException(GameConstants.PLAYER_NOT_FOUND_EXCEPTION);
        }
        var game = callerPlayer.getGame();
        return new GameAdapter(game).getCurrentPlayer();
    }

    public void processPlayerMove(PlayerMoveResponseDto moves, SimpMessageHeaderAccessor accessor, Game game, int moveIndex, Player currentPlayer) {
        var processMoveStrategy = playerMoveStrategySelector.chooseStrategy(currentPlayer.getPlayerType());
        processMoveStrategy.processPlayerMove(moves, game, accessor, moveIndex);
    }

    public void processGameStatus(PlayerMoveResponseDto moves, Game game, Player player, int moveIndex) {
        var gameStatus = checkWin(game, moveIndex);
        var strategy =  gameStateStrategySelector.chooseStrategy(gameStatus);
        strategy.send(moves, game, player, moveIndex);
    }

    public void processAIPlayers(PlayerMoveResponseDto moves, SimpMessageHeaderAccessor accessor, Game game, int moveIndex) {
        while (gameService.isNextPlayerAIType(game)) {
            var player = new GameAdapter(game).getCurrentPlayer();
            processPlayerMove(moves, accessor, game, moveIndex, player);
            processGameStatus(moves, game, player, moveIndex);
        }
    }

    private CheckWinState checkWin(Game game, int playerMoveIndex) {
        if (gameService.checkWin(game, playerMoveIndex)) {
            return CheckWinState.WIN;
        }

        if (new GameAdapter(game).notContainsEmptyFields()) {
            return CheckWinState.DRAW;
        }
        return CheckWinState.CONTINUE;
    }
}
