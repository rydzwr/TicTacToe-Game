package com.rydzwr.tictactoe.game.selector;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.game.strategy.BuildGameStrategy;
import com.rydzwr.tictactoe.game.strategy.ErrorGameTypeStrategy;
import com.rydzwr.tictactoe.game.strategy.LocalPlayerGameStrategy;
import com.rydzwr.tictactoe.game.strategy.MultiPlayerGameStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

@Service
public class GameStrategySelector {
    private final List<BuildGameStrategy> strategyList = asList(
           new LocalPlayerGameStrategy(),
            new MultiPlayerGameStrategy()
    );

    public BuildGameStrategy chooseStrategy(GameDto gameDto) {
        return strategyList
                .stream()
                .filter(strategy -> strategy.applies(gameDto))
                .findFirst()
                .orElse(new ErrorGameTypeStrategy());
    }
}
