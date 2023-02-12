package com.rydzwr.tictactoe.service.game.strategy;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.ServiceTestsHelper;
import com.rydzwr.tictactoe.service.game.strategy.gameBuilder.LocalPlayerGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.gameBuilder.MultiPlayerGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.selector.GameBuilderStrategySelector;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@Import(ServiceTestConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class GameBuilderStrategyTest {

    @Autowired
    private GameBuilderStrategySelector strategySelector;
    @Autowired
    private LocalPlayerGameStrategy localPlayerGameStrategy;
    @Autowired
    private MultiPlayerGameStrategy multiPlayerGameStrategy;
    @Autowired
    private ServiceTestsHelper testsHelper;

    @Test
    @DisplayName("Should Choose Local Game Strategy")
    public void testChooseLocalStrategy() {
        // GIVEN
        var gameDto = testsHelper.buildGameDto(3, 3, 5, 5);

        // WHEN
        var toTest = strategySelector.chooseStrategy(gameDto);

        // THEN
        assertTrue(toTest instanceof LocalPlayerGameStrategy);
    }

    @Test
    @DisplayName("Should Choose Online Game Strategy")
    public void testChooseMultiStrategy() {
        // GIVEN
        var gameDto = testsHelper.buildGameDto(3, 3, 2);

        // WHEN
        var toTest = strategySelector.chooseStrategy(gameDto);

        // THEN
        assertTrue(toTest instanceof MultiPlayerGameStrategy);
    }

    @Test
    @DisplayName("Should Build Local Game")
    public void localPlayerGameStrategyTest() {
        // GIVEN
        final String GAME_BUILDER_STRATEGY_TEST_ONE = "gameBuilderStrategyTestOne";
        testsHelper.buildTestUser(GAME_BUILDER_STRATEGY_TEST_ONE);

        var gameDto = testsHelper.buildGameDto(3, 3,2, 0);

        // WHEN
        var toTest = localPlayerGameStrategy.buildGame(gameDto, GAME_BUILDER_STRATEGY_TEST_ONE);

        // THEN
        assertEquals(3, toTest.getGameSize());
        assertEquals(3, toTest.getDifficulty());
        assertEquals(GameState.IN_PROGRESS, toTest.getState());
    }

    @Test
    @DisplayName("Should Build Online Game")
    public void multiPlayerGameStrategyTest() {
        // GIVEN
        final String GAME_BUILDER_STRATEGY_TEST_TWO = "gameBuilderStrategyTestTwo";
        testsHelper.buildTestUser(GAME_BUILDER_STRATEGY_TEST_TWO);
        var gameDto = testsHelper.buildGameDto(3, 3,2);

        // WHEN
        var toTest = multiPlayerGameStrategy.buildGame(gameDto, GAME_BUILDER_STRATEGY_TEST_TWO);

        // THEN
        assertEquals(3, toTest.getGameSize());
        assertEquals(3, toTest.getDifficulty());
        assertEquals(GameState.AWAITING_PLAYERS, toTest.getState());
    }
}




