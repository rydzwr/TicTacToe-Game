package com.rydzwr.tictactoe.game.constants;

import java.util.List;

import static java.util.Arrays.asList;

public class GameConstants {
    public static final String INVALID_GAME_TYPE_EXCEPTION = "Invalid Game Type";
    public static final String COULD_NOT_FIND_USER_EXCEPTION = "User Not Found";
    public static final String SINGLE_PLAYER_GAME_NAME = "singlePlayer";
    public static final String MULTI_PLAYER_GAME_NAME = "multiPlayer";
    public static final String AI_OPPONENTS_GAME_NAME = "aiOpponents";
    public static final String MIXED_OPPONENTS_GAME_NAME = "mixedOpponents";
    public static final List<String> validGameTypeNames = asList(
            SINGLE_PLAYER_GAME_NAME,
            MULTI_PLAYER_GAME_NAME,
            AI_OPPONENTS_GAME_NAME,
            MIXED_OPPONENTS_GAME_NAME
    );
}
