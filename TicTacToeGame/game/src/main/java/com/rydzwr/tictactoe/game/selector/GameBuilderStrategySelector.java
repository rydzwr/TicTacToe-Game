package com.rydzwr.tictactoe.game.selector;

import com.rydzwr.tictactoe.database.dto.incoming.GameDto;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.BuildGameStrategy;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.ErrorGameTypeStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameBuilderStrategySelector {
    @Autowired
    private List<BuildGameStrategy> strategyList;

    public BuildGameStrategy chooseStrategy(GameDto gameDto) {
        return strategyList
                .stream()
                .filter(strategy -> strategy.applies(gameDto))
                .findFirst()
                .orElse(new ErrorGameTypeStrategy());
    }
}
