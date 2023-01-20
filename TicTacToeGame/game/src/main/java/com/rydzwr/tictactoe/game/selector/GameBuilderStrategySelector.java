package com.rydzwr.tictactoe.game.selector;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.BuildGameStrategy;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.ErrorGameTypeStrategy;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.LocalPlayerGameStrategy;
import com.rydzwr.tictactoe.game.strategy.gameBuilder.MultiPlayerGameStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameBuilderStrategySelector {
    private final LocalPlayerGameStrategy localPlayerGameStrategy;
    private final MultiPlayerGameStrategy multiPlayerGameStrategy;
    private List<BuildGameStrategy> strategyList;
    @PostConstruct
    private void init() {
        strategyList = initList();
    }

    public GameBuilderStrategySelector(LocalPlayerGameStrategy localPlayerGameStrategy, MultiPlayerGameStrategy multiPlayerGameStrategy) {
        this.localPlayerGameStrategy = localPlayerGameStrategy;
        this.multiPlayerGameStrategy = multiPlayerGameStrategy;
    }

    public BuildGameStrategy chooseStrategy(GameDto gameDto) {
        return strategyList
                .stream()
                .filter(strategy -> strategy.applies(gameDto))
                .findFirst()
                .orElse(new ErrorGameTypeStrategy());
    }

    private List<BuildGameStrategy> initList(){
        List<BuildGameStrategy> out = new ArrayList<>();
        out.add(localPlayerGameStrategy);
        out.add(multiPlayerGameStrategy);
        return out;
    }
}
