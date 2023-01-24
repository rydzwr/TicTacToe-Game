package com.rydzwr.tictactoe.web.service;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.dto.outgoing.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.selector.PlayerMoveStrategySelector;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.ProcessMoveStrategy;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
                Game gameAfterAi = processPlayerMove(accessor, playerMoveDto, gameService.getCurrentPlayer(game));

                char playerAfterAI = gameService.getCurrentPlayer(gameAfterAi).getPawn();
                String gameBoardWithAIMove = gameAfterAi.getGameBoard();

                if (gameService.containsEmptyFields(game)) {
                    processDraw();
                }

                template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(gameBoardWithAIMove, playerAfterAI));
            } while (gameService.isNextPlayerAIType(game));
        }
        return game;
    }

    public void checkWin(Game game) {
        if (gameService.checkWin(game)) {
            gameService.processGameWinning(game);

            if (gameService.containsEmptyFields(game)) {
               processDraw();
            }
            Player winner = gameService.getCurrentPlayer(game);
            template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.FINISHED.name(), winner.getPawn()));
        }
    }

    public void sendUpdatedGame(Game game) {
        // SENDING UPDATED GAME BOARD TO FRONTEND
        String updatedGameBoard = game.getGameBoard();
        char nextPlayerPawn = gameService.getCurrentPlayer(game).getPawn();
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(updatedGameBoard, nextPlayerPawn));
    }

    private void processDraw() {
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.FINISHED_DRAW.name()));
    }
}
