package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.web.handler.WebSocketExceptionHandler;
import com.rydzwr.tictactoe.web.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSocketController {
    private final WebSocketExceptionHandler exceptionHandler;
    private final WebSocketService webSocketService;

    @MessageMapping("/gameMove")
    public void send(PlayerMoveDto playerMoveDto, SimpMessageHeaderAccessor accessor) {

        Player currentPlayer;
        try {
            currentPlayer = webSocketService.getCurrentPlayer(accessor);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
            return;
        }

        Game gameAfterAIMove = null;
        try {
            Game gameAfterCallerMove = webSocketService.processPlayerMove(accessor, playerMoveDto, currentPlayer);
            gameAfterAIMove = webSocketService.processAIPlayers(accessor, gameAfterCallerMove, playerMoveDto);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
        }

        assert gameAfterAIMove != null;
        webSocketService.processGameStatus(gameAfterAIMove, currentPlayer);
    }
}
