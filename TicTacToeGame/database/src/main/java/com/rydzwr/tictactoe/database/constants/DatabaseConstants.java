package com.rydzwr.tictactoe.database.constants;

import java.util.List;

public class DatabaseConstants {
    public static final String DEFAULT_BOARD_VALUE = "-";
    public static final String GAME_DIFFICULTY_IS_GRATER_THAN_GAME_SIZE = "Game Difficulty Cannot Be Grater Than Game Size";

    public static final List<String> playerTypes = List.of(PlayerType.AI.name(), PlayerType.LOCAL.name(), PlayerType.ONLINE.name());
}
