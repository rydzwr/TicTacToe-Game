package com.rydzwr.tictactoe.web.service;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.constants.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendMovesDto(PlayerMoveResponseDto moves) {
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, moves);
    }

    public void sendGameResult(GameStateDto state) {
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, state);
    }

    public void updateAwaitingPlayersLobby(int availableGameSlots) {
        template.convertAndSend(WebConstants.WEB_SOCKET_AWAITING_PLAYERS_ENDPOINT, availableGameSlots);
    }

    public void startOnlineGame() {
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.IN_PROGRESS.name()));
    }
}
