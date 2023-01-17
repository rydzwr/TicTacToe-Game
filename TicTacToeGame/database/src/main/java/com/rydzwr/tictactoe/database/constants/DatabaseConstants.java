package com.rydzwr.tictactoe.database.constants;

import java.util.List;

import static java.util.Arrays.asList;

public class DatabaseConstants {

    public static final String PLAYER_BUILD_EXCEPTION_VALUE = "The Player Has To Be Assigned To the Game and Has His Own Pawn";
    public static final String INVALID_PLAYER_TYPE = "Invalid Player Type";
    public static final String DEFAULT_BOARD_VALUE = "-";
    public static final String GAME_DIFFICULTY_IS_GRATER_THAN_GAME_SIZE = "Game Difficulty Cannot Be Grater Than Game Size";

    public static final String PERSON_PLAYER_TYPE = "personPlayer";
    public static final String AI_PLAYER_TYPE = "aiPlayer";
    public static final List<String> playerTypes = asList(
            PERSON_PLAYER_TYPE,
            AI_PLAYER_TYPE
    );
}
