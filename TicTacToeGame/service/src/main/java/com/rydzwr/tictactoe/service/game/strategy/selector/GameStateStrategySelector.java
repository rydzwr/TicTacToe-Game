package com.rydzwr.tictactoe.service.game.strategy.selector;

import com.rydzwr.tictactoe.service.dto.outgoing.gameState.CheckWinState;
import com.rydzwr.tictactoe.service.game.strategy.gameState.GameStateStrategy;
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
