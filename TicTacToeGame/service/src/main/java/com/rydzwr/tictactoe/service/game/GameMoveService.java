package com.rydzwr.tictactoe.service.game;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.strategy.selector.GameStateStrategySelector;
import com.rydzwr.tictactoe.service.game.strategy.selector.PlayerMoveStrategySelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameMoveService {
    private final GameService gameService;
    private final PlayerMoveStrategySelector playerMoveStrategySelector;
    private final GameStateStrategySelector gameStateStrategySelector;
    private final WebSocketService webSocketService;

    public Player getCurrentPlayer(SimpMessageHeaderAccessor accessor) {
        Player callerPlayer = gameService.retrieveAnyPlayerFromUser(accessor);
        if (callerPlayer == null) {
            throw new IllegalArgumentException(GameConstants.PLAYER_NOT_FOUND_EXCEPTION);
        }
        var game = callerPlayer.getGame();

        if (game == null) {
            throw new IllegalArgumentException(GameConstants.GAME_NOT_FOUND_EXCEPTION);
        }

        return new GameAdapter(game).getCurrentPlayer();
    }

    @Transactional
    public void processPlayerMove(PlayerMoveResponseDto moves, SimpMessageHeaderAccessor accessor, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto, Player currentPlayer) {
        var processMoveStrategy = playerMoveStrategySelector.chooseStrategy(currentPlayer.getPlayerType());
        processMoveStrategy.processPlayerMove(moves, gameAdapter, accessor, moveCoordsDto);

        while (gameAdapter.isNextPlayerAIType()) {
            var player = gameAdapter.getCurrentPlayer();
            processGameStatus(moves, gameAdapter, player, moveCoordsDto);
            processPlayerMove(moves, accessor, gameAdapter, moveCoordsDto, player);
        }
    }

    public void processGameStatus(PlayerMoveResponseDto moves, GameAdapter gameAdapter, Player player, MoveCoordsDto moveCoordsDto) {
        var gameStatus = checkWin(gameAdapter, moveCoordsDto);
        var gameStatusStrategy = gameStateStrategySelector.chooseStrategy(gameStatus);
        var response = gameStatusStrategy.resolve(moves, gameAdapter, player, moveCoordsDto);

        if (response instanceof GameStateDto) {
            webSocketService.sendGameResult((GameStateDto) response);
        } else webSocketService.sendMovesDto((PlayerMoveResponseDto) response);
    }

    private CheckWinState checkWin(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        if (gameService.checkWin(gameAdapter, moveCoordsDto)) {
            return CheckWinState.WIN;
        }

        if (gameAdapter.notContainsEmptyFields()) {
            return CheckWinState.DRAW;
        }
        return CheckWinState.CONTINUE;
    }
}
