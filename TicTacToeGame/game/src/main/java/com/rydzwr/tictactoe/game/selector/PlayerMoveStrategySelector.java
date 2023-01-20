package com.rydzwr.tictactoe.game.selector;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.PlayerMoveDto;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.BuildGameStrategy;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.ErrorGameTypeStrategy;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.LocalPlayerGameStrategy;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.MultiPlayerGameStrategy;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.AIPlayerMoveStrategy;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.HumanPlayerMoveStrategy;
import com.rydzwr.tictactoe.game.strategy.moveProcessor.ProcessMoveStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerMoveStrategySelector {
    private final AIPlayerMoveStrategy aiPlayerMoveStrategy;
    private final HumanPlayerMoveStrategy humanPlayerMoveStrategy;
    private List<ProcessMoveStrategy> strategyList;
    @PostConstruct
    private void init() {
        strategyList = initList();
    }

    public PlayerMoveStrategySelector(AIPlayerMoveStrategy aiPlayerMoveStrategy, HumanPlayerMoveStrategy humanPlayerMoveStrategy) {
        this.aiPlayerMoveStrategy = aiPlayerMoveStrategy;
        this.humanPlayerMoveStrategy = humanPlayerMoveStrategy;
    }

    public ProcessMoveStrategy chooseStrategy(PlayerType playerType) {

        // TODO CREATE PROPER ERROR STRATEGY

        return strategyList
                .stream()
                .filter(strategy -> strategy.applies(playerType))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private List<ProcessMoveStrategy> initList(){
        List<ProcessMoveStrategy> out = new ArrayList<>();
        out.add(aiPlayerMoveStrategy);
        out.add(humanPlayerMoveStrategy);
        return out;
    }
}
