package com.rydzwr.tictactoe.service.game.strategy.selector;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.game.strategy.moveProcessor.ErrorPlayerMoveStrategy;
import com.rydzwr.tictactoe.service.game.strategy.moveProcessor.ProcessMoveStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerMoveStrategySelector {
    @Autowired
    private List<ProcessMoveStrategy> strategyList;

    public ProcessMoveStrategy chooseStrategy(PlayerType playerType) {

        return strategyList
                .stream()
                .filter(strategy -> strategy.applies(playerType))
                .findFirst()
                .orElse(new ErrorPlayerMoveStrategy());
    }
}
