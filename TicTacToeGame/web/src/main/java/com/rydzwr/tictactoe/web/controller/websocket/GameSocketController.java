package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.WebSocketService;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.web.handler.WebSocketExceptionHandler;
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
    public void send(MoveCoordsDto moveCoordsDto, SimpMessageHeaderAccessor accessor) {

        Player currentPlayer;
        try {
            currentPlayer = webSocketService.getCurrentPlayer(accessor);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
            return;
        }

        var game = currentPlayer.getGame();
        var gameAdapter = new GameAdapter(game);
        var moves = new PlayerMoveResponseDto();


        // TODO CALL IN ONE TRANSACTION
        try {
            webSocketService.processPlayerMove(moves, accessor, gameAdapter, moveCoordsDto, currentPlayer);
            webSocketService.processAIPlayers(moves, accessor, gameAdapter, moveCoordsDto);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
        }

        webSocketService.processGameStatus(moves, gameAdapter, currentPlayer, moveCoordsDto);
    }
}
