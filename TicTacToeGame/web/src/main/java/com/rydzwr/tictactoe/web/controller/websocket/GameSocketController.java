package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.WebService;
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
    private final WebService webService;

    @MessageMapping("/gameMove")
    public void send(MoveCoordsDto moveCoordsDto, SimpMessageHeaderAccessor accessor) {

        Player currentPlayer;
        try {
            currentPlayer = webService.getCurrentPlayer(accessor);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
            return;
        }

        var game = currentPlayer.getGame();
        var gameAdapter = new GameAdapter(game);
        var moves = new PlayerMoveResponseDto();

        try {
            webService.processPlayerMove(moves, accessor, gameAdapter, moveCoordsDto, currentPlayer);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
        }

        webService.processGameStatus(moves, gameAdapter, currentPlayer, moveCoordsDto);
    }
}
