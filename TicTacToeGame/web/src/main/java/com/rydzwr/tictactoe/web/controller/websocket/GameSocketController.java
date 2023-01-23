package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.dto.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.GameStateDto;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.selector.PlayerMoveStrategySelector;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.ProcessMoveStrategy;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import com.rydzwr.tictactoe.web.handler.WebSocketExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSocketController {
    private final GameService gameService;
    private final SimpMessagingTemplate template;
    private final WebSocketExceptionHandler exceptionHandler;
    private final PlayerMoveStrategySelector playerMoveStrategySelector;

    @Transactional
    @MessageMapping("/gameMove")
    public void send(PlayerMoveDto playerMoveDto, SimpMessageHeaderAccessor accessor) {

        Player callerPlayer = gameService.retrieveAnyPlayerFromUser(accessor);
        if (callerPlayer == null) {
            exceptionHandler.sendException(template, GameConstants.PLAYER_NOT_FOUND_EXCEPTION);
            return;
        }

        Player currentPlayer = gameService.getCurrentPlayer(callerPlayer.getGame());

        if (!gameService.emptySpacesLeft(currentPlayer.getGame())) {
            template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.FINISHED_DRAW.name(), currentPlayer.getPawn()));
            return;
        }

        // PROCESSING CALLER MOVE
        ProcessMoveStrategy processMoveStrategy = playerMoveStrategySelector.chooseStrategy(currentPlayer.getPlayerType());
        Game updatedGame;
        try {
            updatedGame = processMoveStrategy.processPlayerMove(currentPlayer.getGame(), accessor, playerMoveDto);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(template, e.getMessage());
            return;
        }

        // IF NEXT PLAYER IS AI PLAYER, ALREADY PROCESSING HIS MOVE
        if (gameService.isPlayerAIType(updatedGame)) {
            do {
                Player aiPlayer = gameService.getCurrentPlayer(updatedGame);
                ProcessMoveStrategy processMoveStrategyForAI = playerMoveStrategySelector.chooseStrategy(aiPlayer.getPlayerType());
                Game gameAfterAiMove;
                try {
                    gameAfterAiMove = processMoveStrategyForAI.processPlayerMove(updatedGame, accessor, playerMoveDto);
                } catch (IllegalArgumentException e) {
                    template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.FINISHED_DRAW.name(), currentPlayer.getPawn()));
                    return;
                }


                char playerAfterAI = gameService.getCurrentPlayer(gameAfterAiMove).getPawn();
                String gameBoardWithAIMove = gameAfterAiMove.getGameBoard();

                if (gameAfterAiMove.getState().equals(GameState.FINISHED)) {
                    template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.FINISHED.name(), playerAfterAI));
                    return;
                }

                template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(gameBoardWithAIMove, playerAfterAI));
            } while (gameService.isPlayerAIType(updatedGame));
        }


        // IF PLAYER WON SENDING RESULT TO OTHER ENDPOINT
        if (gameService.checkWin(updatedGame)) {
            gameService.processGameWinning(updatedGame);

            if (!updatedGame.getGameBoard().contains("-")) {
                template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.FINISHED_DRAW.name(), currentPlayer.getPawn()));
                return;
            }

            template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.FINISHED.name(), currentPlayer.getPawn()));
            return;
        }

        // SENDING UPDATED GAME BOARD TO FRONTEND
        String updatedGameBoard = updatedGame.getGameBoard();
        char nextPlayerPawn = gameService.getCurrentPlayer(updatedGame).getPawn();
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(updatedGameBoard, nextPlayerPawn));
    }
}
