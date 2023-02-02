package com.rydzwr.tictactoe.service.game.strategy.gameState;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.outgoing.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.GameResultDto;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.GameService;
import com.rydzwr.tictactoe.service.game.constants.WebConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WinGameStrategy implements GameStateStrategy {
    private final GameService gameService;
    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void send(PlayerMoveResponseDto moves, Game game, Player player, int playerMoveIndex) {
        gameService.deleteFinishedGame(game);
        var gameStateDto = new GameStateDto(GameState.FINISHED.name(), new GameResultDto("WIN", player.getPawn()));
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, gameStateDto);
    }

    @Override
    public boolean applies(CheckWinState checkWinState) {
        return checkWinState.equals(CheckWinState.WIN);
    }
}