package com.rydzwr.tictactoe.service.game.strategy;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.algorithm.InviteCodeGenerator;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import com.rydzwr.tictactoe.service.game.strategy.gameBuilder.BuildGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.gameBuilder.LocalPlayerGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.gameBuilder.MultiPlayerGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.selector.GameBuilderStrategySelector;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GameBuilderStrategyTest {

    @Mock
    private GameDatabaseService gameDatabaseService;

    @Mock
    private GameBuilderService gameBuilderService;

    @Mock
    private InviteCodeGenerator inviteCodeGenerator;

    @InjectMocks
    private MultiPlayerGameStrategy multiPlayerGameStrategy;

    @InjectMocks
    private LocalPlayerGameStrategy localPlayerGameStrategy;

    @Test
    public void testChooseLocalStrategy() {
        List<BuildGameStrategy> strategyList = List.of(localPlayerGameStrategy);
        var selector = new GameBuilderStrategySelector();

        // Use ReflectionTestUtils to set the 'strategyList' field in the selector
        ReflectionTestUtils.setField(selector, "strategyList", strategyList);

        var gameDto = new GameDto();
        var playerDto1 = new PlayerDto();
        playerDto1.setPlayerType(PlayerType.LOCAL.name());

        gameDto.setPlayers(List.of(playerDto1));

        var chosenStrategy = selector.chooseStrategy(gameDto);

        MatcherAssert.assertThat(chosenStrategy, Matchers.instanceOf(LocalPlayerGameStrategy.class));
    }

    @Test
    public void testChooseMultiStrategy() {
        List<BuildGameStrategy> strategyList = List.of(multiPlayerGameStrategy);
        var selector = new GameBuilderStrategySelector();

        // Use ReflectionTestUtils to set the 'strategyList' field in the selector
        ReflectionTestUtils.setField(selector, "strategyList", strategyList);

        var gameDto = new GameDto();
        var playerDto1 = new PlayerDto();
        playerDto1.setPlayerType(PlayerType.ONLINE.name());

        gameDto.setPlayers(List.of(playerDto1));

        var chosenStrategy = selector.chooseStrategy(gameDto);

        MatcherAssert.assertThat(chosenStrategy, Matchers.instanceOf(MultiPlayerGameStrategy.class));
    }
}




