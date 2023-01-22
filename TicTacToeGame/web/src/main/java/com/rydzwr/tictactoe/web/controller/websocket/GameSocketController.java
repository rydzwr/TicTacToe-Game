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
import com.rydzwr.tictactoe.database.service.PlayerDatabaseService;
import com.rydzwr.tictactoe.database.service.UserDatabaseService;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.selector.PlayerMoveStrategySelector;
import com.rydzwr.tictactoe.game.selector.PlayerPawnRandomSelector;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.ProcessMoveStrategy;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import com.rydzwr.tictactoe.web.handler.WebSocketExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSocketController {
    private final GameService gameService;
    private final PlayerDatabaseService playerDatabaseService;
    private final GameDatabaseService gameDatabaseService;
    private final SimpMessagingTemplate template;
    private final WebSocketExceptionHandler exceptionHandler;
    private final PlayerMoveStrategySelector playerMoveStrategySelector;

    @Transactional
    @MessageMapping("/gameMove")
    public void send(PlayerMoveDto playerMoveDto, SimpMessageHeaderAccessor accessor) {

        Player caller = gameService.retrieveCallerPlayer(accessor);

        if (caller == null) {
            // TODO CREATE EXCEPTION ENDPOINT ON FRONTEND
            exceptionHandler.sendException(template, GameConstants.PLAYER_NOT_FOUND_EXCEPTION);
            return;
        }

        List<Character> occupiedPawns = caller.getGame().getPlayers().stream().map(Player::getPawn).toList();
        log.info("ALL PAWNS IN GAME: --> ");
        log.info(occupiedPawns.toString());

        Player currentPlayer = gameService.getCurrentPlayer(caller.getGame());

        User callerUser = gameService.retrieveCallerUser(accessor);
        Player callerPlayer = playerDatabaseService.findFirstByUser(callerUser);

        if (callerPlayer.getPlayerType().equals(PlayerType.ONLINE) && callerPlayer.getPawn() != currentPlayer.getPawn()) {
            log.info("REJECTING CALL");
            log.info("CURRENT PLAYER PAWN: --> {}", currentPlayer.getPawn());
            log.info("CALLER PLAYER PAWN: --> {}", callerPlayer.getPawn());
            String sameGameBoard = currentPlayer.getGame().getGameBoard();
            char samePawn = currentPlayer.getPawn();
            template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(sameGameBoard, samePawn));
            return;
        }

        log.info("SOCKET CONTROLLER: --> Caller {}", currentPlayer.getPawn());

        // PROCESSING CALLER MOVE
        ProcessMoveStrategy processMoveStrategy = playerMoveStrategySelector.chooseStrategy(currentPlayer.getPlayerType());
        Game updatedGame = processMoveStrategy.processPlayerMove(currentPlayer.getGame(), playerMoveDto);


        // IF NEXT PLAYER IS AI PLAYER, ALREADY PROCESSING HIS MOVE
        if (gameService.isPlayerAIType(updatedGame)) {
            do {
                Player aiPlayer = gameService.getCurrentPlayer(updatedGame);
                log.info("GAME SERVICE: --> Current Player Pawn {}", aiPlayer.getPawn());
                ProcessMoveStrategy processMoveStrategyForAI = playerMoveStrategySelector.chooseStrategy(aiPlayer.getPlayerType());
                Game gameAfterAiMove = processMoveStrategyForAI.processPlayerMove(updatedGame, playerMoveDto);


                char playerAfterAI = gameService.getCurrentPlayer(gameAfterAiMove).getPawn();
                String gameBoardWithAIMove = gameAfterAiMove.getGameBoard();

                gameDatabaseService.save(gameAfterAiMove);
                template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(gameBoardWithAIMove, playerAfterAI));
            } while (gameService.isPlayerAIType(updatedGame));
        }


        // IF PLAYER WON SENDING RESULT TO OTHER ENDPOINT
        if (gameService.checkWin(updatedGame)) {
            gameService.processGameWinning(updatedGame);

            // TODO SEND PROPER MESSAGE IF DRAW
            // TODO JUST AFTER RECEIVE FINISHED GAME STATE CHANGE COMPONENT AND SET ROUTE GUARD ON FRONTEND
            template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateDto(GameState.FINISHED.name(), currentPlayer.getPawn()));
            return;
        }

        // SENDING UPDATED GAME BOARD TO FRONTEND
        String updatedGameBoard = updatedGame.getGameBoard();
        gameDatabaseService.save(updatedGame);
        char nextPlayerPawn = gameService.getCurrentPlayer(updatedGame).getPawn();
        template.convertAndSend(WebConstants.WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardDto(updatedGameBoard, nextPlayerPawn));
        log.info("MOVE PROCESSING FINISHED, WAITING FOR FRONTEND CALL");
    }
}
