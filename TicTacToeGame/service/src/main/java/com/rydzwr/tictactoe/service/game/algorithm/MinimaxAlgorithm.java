package com.rydzwr.tictactoe.service.game.algorithm;

import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MinimaxAlgorithm {

    // NO IT'S NOT MINIMAX ALGORITHM ;)

    public int processMove(GameAdapter gameAdapter, char playerPawn) {
        var gameBoard = gameAdapter.getGameBoardCopy();
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

