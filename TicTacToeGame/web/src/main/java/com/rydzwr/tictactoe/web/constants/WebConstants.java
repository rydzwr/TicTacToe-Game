package com.rydzwr.tictactoe.web.constants;

public class WebConstants {
    public static final String WEB_SOCKET_TOPIC_EXCEPTION_ENDPOINT = "/topic/exception";
    public static final String WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT = "/topic/gameBoard";
    public static final String WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT = "/topic/gameState";

    public static final String GAME_VALIDATOR_EXCEPTION = "Invalid Data Received";
    public static final String USER_IS_NOT_ASSIGNED_TO_GAME_EXCEPTION = "User Is Not Assigned To Game";
    public static final String COULD_NOT_JOIN_GAME = "Couldn't Join Game";
    public static final String PREV_GAME_NOT_FOUND = "Previous Game Not Found";
}
