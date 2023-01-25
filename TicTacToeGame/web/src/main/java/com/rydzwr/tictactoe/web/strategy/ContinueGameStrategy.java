package com.rydzwr.tictactoe.web.strategy;

import com.rydzwr.tictactoe.database.dto.outgoing.GameBoardDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.web.constants.CheckWinState;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContinueGameStrategy implements GameStateStrategy{
    private final GameService gameService;
    @Override
    public void send(Game game, Player player, SimpMessagingTemplate template) {
        String updatedGameBoard = game.getGameBoard();
        var nexPlayer = gameService.getCurrentPlayer(game);
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(updatedGameBoard, nexPlayer.getPawn()));
    }

    @Override
    public boolean applies(CheckWinState checkWinState) {
        return checkWinState.equals(CheckWinState.CONTINUE);
    }
}
