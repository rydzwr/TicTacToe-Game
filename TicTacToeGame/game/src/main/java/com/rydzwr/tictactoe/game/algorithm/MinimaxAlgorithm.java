package com.rydzwr.tictactoe.game.algorithm;

import com.rydzwr.tictactoe.game.constants.GameConstants;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MinimaxAlgorithm {

    // NO IT'S NOT MINIMAX ALGORITHM ;)

    public int processMove(String gameBoard, char playerPawn) {

        if (!gameBoard.contains("-")) {
            throw new IllegalArgumentException(GameConstants.ALL_FIELDS_ON_BOARD_OCCUPIED_EXCEPTION);
        }

        // TODO ZMIENIAĆ BOARDA MOŻE TYLKO USŁUGA, KTÓRA ZAWSZE SPRAWDZI CZY WSTAWIAMY W PUSTE POLE
        // TODO JEŚLI NIE MA PUSTYCH MIEJSC TO GRA KOŃCZY SIĘ REMISEM, DECYDUJE O TYM LOGIKA KTÓRA PROWADZI GRĘ

        int moveIndex = -1;
        char[] board = gameBoard.toCharArray();
        Random rand = new Random();
        int emptySpaces = 0;
        //count the number of empty spaces
        for (char c : board) {
            if (c == '-') {
                emptySpaces++;
            }
        }
        if(emptySpaces > 0) {
            int move = rand.nextInt(emptySpaces);
            for (int i = 0; i < board.length; i++) {
                if (board[i] == '-') {
                    if(move == 0) {
                        board[i] = playerPawn;
                        moveIndex = i;
                        break;
                    }
                    move--;
                }
            }
        }
        return moveIndex;
    }

}

