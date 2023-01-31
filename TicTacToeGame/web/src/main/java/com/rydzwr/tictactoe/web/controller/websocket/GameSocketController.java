package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.web.handler.WebSocketExceptionHandler;
import com.rydzwr.tictactoe.web.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSocketController {
    private final WebSocketExceptionHandler exceptionHandler;
    private final WebSocketService webSocketService;

    @MessageMapping("/gameMove")
    public void send(PlayerMoveDto playerMoveDto, SimpMessageHeaderAccessor accessor) {

        // TODO DB MODULE SHOULD CONTAIN ONLY ENTITIES AND REPOS

        Player currentPlayer;
        try {
            currentPlayer = webSocketService.getCurrentPlayer(accessor);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
            return;
        }

        var game = currentPlayer.getGame();
        var moves = new PlayerMoveResponseDto();

        try {

            // TODO PROCESS IN ONE DB TRANSACTION
            webSocketService.processPlayerMove(moves, accessor, game, playerMoveDto, currentPlayer);
            webSocketService.processAIPlayers(moves, accessor, game, playerMoveDto);
        } catch (IllegalArgumentException e) {
            exceptionHandler.sendException(e.getMessage());
        }

        assert game != null;
        webSocketService.processGameStatus(moves, game, currentPlayer, playerMoveDto.getGameBoardElementIndex());
    }
}
