package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.dto.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.GameStateDto;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import com.rydzwr.tictactoe.game.exception.ExceptionModel;
import com.rydzwr.tictactoe.game.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSocketController {
    private final GameService gameService;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate template;

    @Transactional
    @MessageMapping("/gameMove")
    public void send(PlayerMoveDto playerMoveDto, SimpMessageHeaderAccessor accessor) {

        // GETTING USER FROM SECURITY CONTEXT
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        User user = userRepository.findByName(username);

        // IT'S BARELY POSSIBLE< BUT CHECKING JUST IN CASE
        assert user != null;

        // FINDING HIS (ONE -> TO -> ONE) PLAYER
        Player player = playerRepository.findFirstByUser(user);

        if (player == null) {
            // TODO CREATE EXCEPTION ENDPOINT ON FRONTEND
            template.convertAndSend("/topic/exception", new ExceptionModel("Player Not Found, game has been finished"));
            throw new IllegalArgumentException("Player Not Found ( GAME HAS BEEN DELETED OR NEVER EXIST)");
        }

        // PROCESSING GAME MOVE
        Game game =  gameService.processPlayerMove(player.getGame(), playerMoveDto, template);

        // SENDING UPDATED GAME BOARD TO FRONTEND
        String updatedGameBoard = game.getGameBoard();
        template.convertAndSend("/topic/gameBoard", new GameBoardDto(updatedGameBoard));

        // CHECKING IF CURRENT PLAYER WON
        boolean gameOver = gameService.checkWin(game);

        // IF PLAYER WON SENDING RESULT TO OTHER ENDPOINT
        if (gameOver) {
            char winnerPawn = gameService.getWonPawn(game);
            gameRepository.delete(game);

            // TODO JUST AFTER RECEIVE FINISHED GAME STATE CHANGE COMPONENT AND SET ROUTE GUARD ON FRONTEND
            template.convertAndSend("/topic/gameState", new GameStateDto(GameState.FINISHED.name(), winnerPawn));
        }
    }
}
