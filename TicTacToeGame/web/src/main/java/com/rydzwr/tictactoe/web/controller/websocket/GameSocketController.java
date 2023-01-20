package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.GameStateDto;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.game.algorithm.MinimaxAlgorithm;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.selector.PlayerMoveStrategySelector;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.ProcessMoveStrategy;
import com.rydzwr.tictactoe.game.validator.PlayerMoveDtoValidator;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import com.rydzwr.tictactoe.web.handler.WebSocketExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSocketController {
    private final GameService gameService;
    private final GameDatabaseService gameDatabaseService;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate template;
    private final WebSocketExceptionHandler exceptionHandler;
    private final PlayerMoveStrategySelector playerMoveStrategySelector;

    @Transactional
    @MessageMapping("/gameMove")
    public void send(PlayerMoveDto playerMoveDto, SimpMessageHeaderAccessor accessor) {

        // GETTING USER FROM SECURITY CONTEXT
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        User user = userRepository.findByName(username);

        // IT'S BARELY POSSIBLE BUT CHECKING JUST IN CASE
        assert user != null;

        // FINDING HIS (ONE -> TO -> ONE) PLAYER
        Player player = playerRepository.findFirstByUser(user);

        if (player == null) {
            // TODO CREATE EXCEPTION ENDPOINT ON FRONTEND
            exceptionHandler.sendException(template, GameConstants.PLAYER_NOT_FOUND_EXCEPTION);
            return;
        }

        // ToDO: CHECK IF EP CALLER USER IS THE CURRENT PLAYER IN GAME

        // GETTING PLAYER CHAR BEFORE UPDATING GAME IN CASE OF WIN
        char prevChar = gameService.getCurrentPawn(player.getGame());

        Game game = player.getGame();
        Player currentPlayer = game.getPlayers().stream().filter((type) -> type.getPawn() == prevChar).findFirst().get();
        ProcessMoveStrategy processMoveStrategy = playerMoveStrategySelector.chooseStrategy(currentPlayer.getPlayerType());

        // PROCESSING GAME MOVE
        Game updatedGame = processMoveStrategy.processPlayerMove(player.getGame(), playerMoveDto);

        // IF NEXT PLAYER IS AI PLAYER, PROCESSING HIS MOVE ALREADY
        if (gameService.nextPlayerIsAI(game)) {
            Player nextPlayer = gameService.getNextPlayer(game);
            ProcessMoveStrategy processMoveStrategyForAI = playerMoveStrategySelector.chooseStrategy(nextPlayer.getPlayerType());
            Game gameWithAIMovedAlready = processMoveStrategyForAI.processPlayerMove(game, playerMoveDto);
            char playerAfterAI = gameService.getCurrentPawn(gameWithAIMovedAlready);
            String gameBoardWithAIMove = gameWithAIMovedAlready.getGameBoard();
            template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(gameBoardWithAIMove, playerAfterAI));
        }

        // SENDING UPDATED GAME BOARD TO FRONTEND
        String updatedGameBoard = updatedGame.getGameBoard();
        char nextPlayerPawn = gameService.getCurrentPawn(updatedGame);
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(updatedGameBoard, nextPlayerPawn));

        // CHECKING IF CURRENT PLAYER WON
        boolean gameOver = gameService.checkWin(updatedGame);

        // IF PLAYER WON SENDING RESULT TO OTHER ENDPOINT
        if (gameOver) {
            gameDatabaseService.delete(updatedGame);

            // TODO SEND PROPER MESSAGE IF DRAW

            // TODO JUST AFTER RECEIVE FINISHED GAME STATE CHANGE COMPONENT AND SET ROUTE GUARD ON FRONTEND
            template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.FINISHED.name(), prevChar));
        }
    }
}
