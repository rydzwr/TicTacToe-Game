package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.GameMoveService;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
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
    private final GameMoveService gameMoveService;
    private final WebSocketService webSocketService;

    @MessageMapping("/gameMove")
    public void send(MoveCoordsDto moveCoordsDto, SimpMessageHeaderAccessor accessor) {

        Player currentPlayer;
        try {
            currentPlayer = gameMoveService.getCurrentPlayer(accessor);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
            return;
        }

        var game = currentPlayer.getGame();
        var gameAdapter = new GameAdapter(game);
        var moves = new PlayerMoveResponseDto();

        try {
            gameMoveService.processPlayerMove(moves, accessor, gameAdapter, moveCoordsDto, currentPlayer);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
        }

        var response = gameMoveService.processGameStatus(moves, gameAdapter, currentPlayer, moveCoordsDto);

        if (response instanceof GameStateDto) {
            webSocketService.sendGameResult((GameStateDto) response);
        } else {
            webSocketService.sendMovesDto((PlayerMoveResponseDto) response);
        }
    }
}
