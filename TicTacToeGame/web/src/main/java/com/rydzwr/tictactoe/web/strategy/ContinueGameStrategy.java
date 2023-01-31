package com.rydzwr.tictactoe.web.strategy;

import com.rydzwr.tictactoe.database.dto.outgoing.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.web.constants.CheckWinState;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContinueGameStrategy implements GameStateStrategy{
    private final GameService gameService;

    // TODO TEMPLATE TO @AUTOWIRED

    @Override
    public void send(PlayerMoveResponseDto moves, Game game, Player player, int playerMoveIndex, SimpMessagingTemplate template) {
        var nextPlayer = gameService.getCurrentPlayer(game);
        moves.setCurrentPlayerMove(nextPlayer.getPawn());
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, moves);
    }

    @Override
    public boolean applies(CheckWinState checkWinState) {
        return checkWinState.equals(CheckWinState.CONTINUE);
    }
}
