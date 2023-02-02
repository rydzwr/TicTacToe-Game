package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.service.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.WebSocketService;
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
        var moves = new PlayerMoveResponseDto();

        var index = moveCoordsDto.getIndex(game.getGameSize());

        try {
            webSocketService.processPlayerMove(moves, accessor, game, index, currentPlayer);
            webSocketService.processAIPlayers(moves, accessor, game, index);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
        }

        webSocketService.processGameStatus(moves, game, currentPlayer, moveCoordsDto.getIndex(game.getGameSize()));
    }
}
