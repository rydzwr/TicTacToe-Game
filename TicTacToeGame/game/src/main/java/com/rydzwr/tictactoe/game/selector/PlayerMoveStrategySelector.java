package com.rydzwr.tictactoe.game.selector;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
