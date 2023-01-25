package com.rydzwr.tictactoe.web.strategy;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.dto.outgoing.GameResultDto;
import com.rydzwr.tictactoe.database.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WinGameStrategy implements GameStateStrategy{
    private final GameService gameService;
    @Override
    public void send(Game game, Player player, SimpMessagingTemplate template) {
        gameService.deleteFinishedGame(game);
        var gameStateDto = new GameStateDto(GameState.FINISHED.name(), new GameResultDto("WIN", player.getPawn()));
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, gameStateDto);
    }

    @Override
    public boolean applies(CheckWinState checkWinState) {
        return checkWinState.equals(CheckWinState.WIN);
    }
}
