package com.rydzwr.tictactoe.game.service;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.game.selector.GameStrategySelector;
import com.rydzwr.tictactoe.game.strategy.BuildGameStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameBuilderService {
    private final GameStrategySelector selector;
    public void buildGame(GameDto gameDto) {
        BuildGameStrategy strategy = selector.chooseStrategy(gameDto);
        strategy.buildGame(gameDto);
    }
}
