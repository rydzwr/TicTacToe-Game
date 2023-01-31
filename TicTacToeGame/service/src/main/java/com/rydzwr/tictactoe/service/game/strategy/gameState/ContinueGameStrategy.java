package com.rydzwr.tictactoe.service.game.strategy.gameState;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.outgoing.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.GameService;
import com.rydzwr.tictactoe.service.game.constants.WebConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContinueGameStrategy implements GameStateStrategy {
    private final GameService gameService;
    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void send(PlayerMoveResponseDto moves, Game game, Player player, int playerMoveIndex) {
        var nextPlayer = gameService.getCurrentPlayer(game);
        moves.setCurrentPlayerMove(nextPlayer.getPawn());
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, moves);
    }

    @Override
    public boolean applies(CheckWinState checkWinState) {
        return checkWinState.equals(CheckWinState.CONTINUE);
    }
}
