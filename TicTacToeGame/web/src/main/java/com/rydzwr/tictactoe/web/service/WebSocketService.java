package com.rydzwr.tictactoe.web.service;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.dto.outgoing.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.outgoing.GameResultDto;
import com.rydzwr.tictactoe.database.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.selector.PlayerMoveStrategySelector;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.ProcessMoveStrategy;
import com.rydzwr.tictactoe.web.CheckWinState;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate template;
    private final GameService gameService;
    private final PlayerMoveStrategySelector playerMoveStrategySelector;

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

        if (gameStatus == CheckWinState.PLAY) {
            sendUpdatedGame(game, player);
        }

        if (gameStatus == CheckWinState.WIN) {
            processGameWin(game, player);
        }

        if (gameStatus == CheckWinState.DRAW) {
            processDraw(game);
        }
    }

    private CheckWinState checkWin(Game game) {
        if (gameService.checkWin(game)) {
            return CheckWinState.WIN;
        }

        if (!gameService.containsEmptyFields(game)) {
            return CheckWinState.DRAW;
        }
        return CheckWinState.PLAY;
    }

    private void sendUpdatedGame(Game game, Player player) {
        String updatedGameBoard = game.getGameBoard();
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(updatedGameBoard, player.getPawn()));
    }

    private void processGameWin(Game game, Player winner) {
        gameService.deleteFinishedGame(game);
        var gameStateDto = new GameStateDto(GameState.FINISHED.name(), new GameResultDto("WIN", winner.getPawn()));
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, gameStateDto);
    }

    private void processDraw(Game game) {
        gameService.deleteFinishedGame(game);
        var gameStateDto = new GameStateDto(GameState.FINISHED.name(), new GameResultDto("DRAW", null));
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, gameStateDto);
    }
}
