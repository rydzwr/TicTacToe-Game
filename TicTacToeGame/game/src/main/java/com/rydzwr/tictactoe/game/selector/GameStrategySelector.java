package com.rydzwr.tictactoe.game.selector;

import com.rydzwr.tictactoe.game.strategyforgame.*;

import java.util.List;

import static java.util.Arrays.asList;

public class GameStrategySelector {
    private static List<BuildGameStrategy> strategyList = asList(
            new AIOpponentsGameStrategy(),
            new MixedOpponentsGameStrategy(),
            new MultiPlayerGameStrategy(),
            new SinglePlayerGameStrategy()
    );

    public BuildGameStrategy chooseStrategy(String gameType) {
        if (gameType == null) {
            return new ErrorGameTypeStrategy();
        }

        return strategyList
                .stream()
                .filter(s -> s.applies(gameType))
                .findFirst()
                .orElse(new ErrorGameTypeStrategy());
    }
}
