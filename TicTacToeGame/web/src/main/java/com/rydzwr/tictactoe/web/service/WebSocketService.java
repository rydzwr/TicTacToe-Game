package com.rydzwr.tictactoe.web.service;

import com.rydzwr.tictactoe.database.dto.incoming.PlayerMoveDto;
import com.rydzwr.tictactoe.database.dto.outgoing.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.selector.PlayerMoveStrategySelector;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.ProcessMoveStrategy;
import com.rydzwr.tictactoe.web.constants.CheckWinState;
import com.rydzwr.tictactoe.web.selector.GameStateStrategySelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate template;
    private final GameService gameService;
    private final PlayerMoveStrategySelector playerMoveStrategySelector;
    private final GameStateStrategySelector gameStateStrategySelector;

    public Player getCurrentPlayer(SimpMessageHeaderAccessor accessor) {
        Player callerPlayer = gameService.retrieveAnyPlayerFromUser(accessor);
        if (callerPlayer == null) {
            throw new IllegalArgumentException(GameConstants.PLAYER_NOT_FOUND_EXCEPTION);
        }
        return gameService.getCurrentPlayer(callerPlayer.getGame());
    }

    public void processPlayerMove(PlayerMoveResponseDto moves, SimpMessageHeaderAccessor accessor, Game game, PlayerMoveDto playerMoveDto, Player currentPlayer) {
        ProcessMoveStrategy processMoveStrategy = playerMoveStrategySelector.chooseStrategy(currentPlayer.getPlayerType());
        processMoveStrategy.processPlayerMove(moves, game, accessor, playerMoveDto);
    }

    public void processAIPlayers(PlayerMoveResponseDto moves, SimpMessageHeaderAccessor accessor, Game game, PlayerMoveDto playerMoveDto) {
        while (gameService.isNextPlayerAIType(game)) {
            var player = gameService.getCurrentPlayer(game);
            processPlayerMove(moves, accessor, game, playerMoveDto, player);
            processGameStatus(moves, game, player, playerMoveDto.getGameBoardElementIndex());
        }
    }

    public void processGameStatus(PlayerMoveResponseDto moves, Game game, Player player, int playerMoveIndex) {
        var gameStatus = checkWin(game, playerMoveIndex);

        var strategy =  gameStateStrategySelector.chooseStrategy(gameStatus);
        strategy.send(moves, game, player, playerMoveIndex, template);
    }

    private CheckWinState checkWin(Game game, int playerMoveIndex) {
        if (gameService.checkWin(game, playerMoveIndex)) {
            return CheckWinState.WIN;
        }

        if (!gameService.containsEmptyFields(game)) {
            return CheckWinState.DRAW;
        }
        return CheckWinState.CONTINUE;
    }
}
