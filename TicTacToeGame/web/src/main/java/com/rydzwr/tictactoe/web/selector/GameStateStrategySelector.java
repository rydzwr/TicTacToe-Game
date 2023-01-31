package com.rydzwr.tictactoe.web.selector;

import com.rydzwr.tictactoe.web.constants.CheckWinState;
import com.rydzwr.tictactoe.web.strategy.GameStateStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GameStateStrategySelector {

    @Autowired
    private List<GameStateStrategy> strategyList;


    public GameStateStrategy chooseStrategy(CheckWinState checkWinState) {
        return strategyList
                .stream()
                .filter(strategy -> strategy.applies(checkWinState))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }


}
