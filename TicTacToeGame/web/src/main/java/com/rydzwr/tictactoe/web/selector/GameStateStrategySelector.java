package com.rydzwr.tictactoe.web.selector;

import com.rydzwr.tictactoe.web.constants.CheckWinState;
import com.rydzwr.tictactoe.web.strategy.ContinueGameStrategy;
import com.rydzwr.tictactoe.web.strategy.DrawStateStrategy;
import com.rydzwr.tictactoe.web.strategy.GameStateStrategy;
import com.rydzwr.tictactoe.web.strategy.WinGameStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameStateStrategySelector {
    private final ContinueGameStrategy continueGameStrategy;
    private final DrawStateStrategy drawStateStrategy;
    private final WinGameStrategy winGameStrategy;

    private List<GameStateStrategy> strategyList;

    @PostConstruct
    private void init() {
        strategyList = initList();
    }

    public GameStateStrategySelector(ContinueGameStrategy continueGameStrategy, DrawStateStrategy drawStateStrategy, WinGameStrategy winGameStrategy) {
        this.continueGameStrategy = continueGameStrategy;
        this.drawStateStrategy = drawStateStrategy;
        this.winGameStrategy = winGameStrategy;
    }

    public GameStateStrategy chooseStrategy(CheckWinState checkWinState) {
        return strategyList
                .stream()
                .filter(strategy -> strategy.applies(checkWinState))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private List<GameStateStrategy> initList(){
        List<GameStateStrategy> out = new ArrayList<>();
        out.add(continueGameStrategy);
        out.add(drawStateStrategy);
        out.add(winGameStrategy);
        return out;
    }
}
