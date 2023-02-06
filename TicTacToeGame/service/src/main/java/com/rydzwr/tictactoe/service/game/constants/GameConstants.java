package com.rydzwr.tictactoe.service.game.constants;

import com.rydzwr.tictactoe.database.constants.PlayerType;

import java.util.List;

public class GameConstants {
    public static final String INVALID_GAME_TYPE_EXCEPTION = "Invalid Game Type";
    public static final String PLAYER_NOT_FOUND_EXCEPTION = "Player Not Found ( GAME HAS BEEN DELETED OR NEVER EXIST)";
    public static final String GAME_NOT_FOUND_EXCEPTION = "Game Not Found ( HAS BEEN DELETED OR NEVER EXIST)";
    public static final String PLAYER_MOVE_OUT_OF_BOARD_EXCEPTION = "Invalid Player Move ( OUT OF BOARD )";
    public static final String ALL_FIELDS_ON_BOARD_OCCUPIED_EXCEPTION = "All fields On Board Occupied";
    public static final String ALL_GAME_SLOTS_OCCUPIED_EXCEPTION = "All game slots already occupied";
    public static final String GAME_WITH_GIVEN_CODE_NOT_FOUND_EXCEPTION = "Game with given invite code not found";
    public static final String PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION = "Player Pressed Occupied Field";
    public static final String NOT_CALLER_TURN_EXCEPTION = "Invalid caller (Another Player Turn)";
    public static final String INVALID_PLAYER_TYPE_EXCEPTION = "Invalid Player Type (Cannot Process Move)";
    public static final String PLAYER_PAWNS = "ABCDEFGHIJKLMNOPRSTUWYZ";
    public static final char DEFAULT_STARTING_PAWN = 'X';
    public static final String DEFAULT_BOARD_VALUE = "-";
    public static final List<String> playerTypes = List.of(PlayerType.AI.name(), PlayerType.LOCAL.name(), PlayerType.ONLINE.name());
}
