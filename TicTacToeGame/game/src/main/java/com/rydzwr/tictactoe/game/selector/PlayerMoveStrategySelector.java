package com.rydzwr.tictactoe.game.selector;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerMoveStrategySelector {
    private final AIPlayerMoveStrategy aiPlayerMoveStrategy;
    private final LocalPlayerMoveStrategy localPlayerMoveStrategy;
    private final OnlinePlayerMoveStrategy onlinePlayerMoveStrategy;
    private List<ProcessMoveStrategy> strategyList;
    @PostConstruct
    private void init() {
        strategyList = initList();
    }

    public PlayerMoveStrategySelector(
            AIPlayerMoveStrategy aiPlayerMoveStrategy,
            LocalPlayerMoveStrategy localPlayerMoveStrategy,
            OnlinePlayerMoveStrategy onlinePlayerMoveStrategy
    ) {
        this.aiPlayerMoveStrategy = aiPlayerMoveStrategy;
        this.localPlayerMoveStrategy = localPlayerMoveStrategy;
        this.onlinePlayerMoveStrategy = onlinePlayerMoveStrategy;
    }

    public ProcessMoveStrategy chooseStrategy(PlayerType playerType) {

        return strategyList
                .stream()
                .filter(strategy -> strategy.applies(playerType))
                .findFirst()
                .orElse(new ErrorPlayerMoveStrategy());
    }

    private List<ProcessMoveStrategy> initList(){
        List<ProcessMoveStrategy> out = new ArrayList<>();
        out.add(aiPlayerMoveStrategy);
        out.add(localPlayerMoveStrategy);
        out.add(onlinePlayerMoveStrategy);
        return out;
    }
}
