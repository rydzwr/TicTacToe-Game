package com.rydzwr.tictactoe.web.controller;

import com.rydzwr.tictactoe.database.dto.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.GameStateDto;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.GameState;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSocketController {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate template;

    @MessageMapping("/gameMove")
    public void send(PlayerMoveDto playerMoveDto, SimpMessageHeaderAccessor accessor) throws Exception {
        String username = accessor.getUser().getName();
        User user = userRepository.findByName(username);

        Player player = playerRepository.findFirstByUser(user);
        Game game = player.getGame();

        if (game.getState() != GameState.IN_PROGRESS) {
            throw new IllegalArgumentException("Game is not in progress.");
        }

        String newGameBoard = game.getGameBoard();
        if (newGameBoard.charAt(playerMoveDto.getI()) != '-') {
            template.convertAndSend("/topic/gameBoard", new GameBoardDto(game.getGameBoard()));
        }

        List<Player> players = game.getPlayers();

        int currentPlayerTurn = game.getCurrentPlayerTurn();
        char playerPawn = players.get(currentPlayerTurn).getPawn();
        game.setCurrentPlayerTurn(currentPlayerTurn == players.size() - 1 ? 0 : currentPlayerTurn + 1);

        StringBuilder stringBuilder = new StringBuilder(newGameBoard);
        stringBuilder.setCharAt(playerMoveDto.getI(), playerPawn);
        game.setGameBoard(stringBuilder.toString());

        template.convertAndSend("/topic/gameBoard", new GameBoardDto(game.getGameBoard()));

        boolean gameOver = checkWin(game);
        if (gameOver) {
            //game.setState(GameState.FINISHED);
            gameRepository.delete(game);
            template.convertAndSend("/topic/gameState", new GameStateDto(GameState.FINISHED.toString()));
        } else {
            gameRepository.save(game);
        }
    }

    private boolean checkWin(Game game) {
        int gameSize = game.getGameSize();
        int gameDifficulty = game.getDifficulty();
        String gameBoard = game.getGameBoard();

        // CHECK HORIZONTAL WIN
        for (int row = 0; row < gameSize; row++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, row, 0);
            int counter = 0;
            for (int column = 0; column < gameSize; column++) {
                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if ((pawn == candidate) && (pawn != '-')) {
                    counter++;
                } else {
                    counter = 0;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {
                    return true;
                }
            }
        }

        // CHECK VERTICAL WIN
        for (int column = 0; column < gameSize; column++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, 0, column);
            int counter = 0;
            for (int row = 0; row < gameSize; row++) {
                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if ((pawn == candidate) && (pawn != '-')) {
                    counter++;
                } else {
                    counter = 0;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {
                    return true;
                }
            }
        }

        // CHECK RIGHT-TOP DIAGONALS
      /*  for (int diagonal = 0; diagonal < gameSize; diagonal++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, diagonal, 0);
            int counter = 0;
            for (int i = 0; i < diagonal; i++) {
                int row = diagonal - i;
                int column = i;

                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if (pawn == candidate) {
                    counter++;
                } else {
                    counter = 0;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {
                    return true;
                }
            }
        }

        // CHECK RIGHT-BOTTOM DIAGONALS
        for (int diagonal = 0; diagonal < gameSize; diagonal++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, gameSize - 1, diagonal);
            int counter = 0;
            for (int i = 0; i < gameSize - diagonal; i++) {
                int row = gameSize - 1 - i;
                int column = i;

                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if (pawn == candidate) {
                    counter++;
                } else {
                    counter = 0;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {
                    return true;
                }
            }
        }*/

        return false;
    }

    private char getPawnAtCoords(String board, int gameSize, int row, int column) {
        int index = row * gameSize + column;
        return board.charAt(index);
    }

}
