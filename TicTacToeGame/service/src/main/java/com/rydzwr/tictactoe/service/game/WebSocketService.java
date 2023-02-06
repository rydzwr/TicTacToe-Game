package com.rydzwr.tictactoe.service.game;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.constants.WebConstants;
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

        if (game == null) {
            throw new IllegalArgumentException(GameConstants.GAME_NOT_FOUND_EXCEPTION);
        }

        return new GameAdapter(game).getCurrentPlayer();
    }

    public void processPlayerMove(PlayerMoveResponseDto moves, SimpMessageHeaderAccessor accessor, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto, Player currentPlayer) {
        var processMoveStrategy = playerMoveStrategySelector.chooseStrategy(currentPlayer.getPlayerType());
        processMoveStrategy.processPlayerMove(moves, gameAdapter, accessor, moveCoordsDto);
    }

    public void processGameStatus(PlayerMoveResponseDto moves, GameAdapter gameAdapter, Player player, MoveCoordsDto moveCoordsDto) {
        var gameStatus = checkWin(gameAdapter, moveCoordsDto);
        var strategy = gameStateStrategySelector.chooseStrategy(gameStatus);
        var response = strategy.resolve(moves, gameAdapter, player, moveCoordsDto);

        if (response instanceof GameStateDto) {
            sendGameResult((GameStateDto) response);
        } else sendMovesDto((PlayerMoveResponseDto) response);
    }

    public void processAIPlayers(PlayerMoveResponseDto moves, SimpMessageHeaderAccessor accessor, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        while (gameService.isNextPlayerAIType(gameAdapter)) {
            var player = gameAdapter.getCurrentPlayer();
            processPlayerMove(moves, accessor, gameAdapter, moveCoordsDto, player);
            processGameStatus(moves, gameAdapter, player, moveCoordsDto);
        }
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

    private void sendMovesDto(PlayerMoveResponseDto moves) {
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, moves);
    }

    private void sendGameResult(GameStateDto state) {
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, state);
    }
}
