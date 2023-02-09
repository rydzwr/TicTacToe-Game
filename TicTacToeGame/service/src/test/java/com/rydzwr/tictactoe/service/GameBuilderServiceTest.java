package com.rydzwr.tictactoe.service;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.database.PlayerDatabaseService;
import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Import(ServiceTestConfiguration.class)
@TestPropertySource(locations="classpath:application-test.properties")
@SpringBootTest
public class GameBuilderServiceTest {

    @Mock
    private UserDatabaseService userDatabaseService;
    @Mock
    private PlayerDatabaseService playerDatabaseService;

    @Autowired
    private GameBuilderService gameBuilderService;

    private User caller;
    private Game game;

    @Before
    public void setUp() {
        gameBuilderService = new GameBuilderService(userDatabaseService, playerDatabaseService);
        caller = new User();
        game = new Game();
    }

    @Test
    public void testBuildCallerPlayer() {
        when(userDatabaseService.findByName("username")).thenReturn(caller);

        gameBuilderService.buildCallerPlayer(caller, game, PlayerType.LOCAL);

        Player player = new Player();
        player.setPlayerType(PlayerType.LOCAL);
        player.setPlayerGameIndex(0);
        player.setPawn('X');
        player.setUser(caller);
        player.setGame(game);

        verify(playerDatabaseService).save(player);
    }

    @Test
    public void testBuildLocalPlayers() {
        assertNotNull(gameBuilderService);
    }

}
