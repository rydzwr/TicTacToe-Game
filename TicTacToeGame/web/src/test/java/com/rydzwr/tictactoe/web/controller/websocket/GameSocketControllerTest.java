package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import com.rydzwr.tictactoe.service.security.factory.UserFactory;
import com.rydzwr.tictactoe.web.WebTestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.security.Principal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@AutoConfigureWebClient
@Import(WebTestConfig.class)
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class GameSocketControllerTest {

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private UserDatabaseService userDatabaseService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testSend() {
        MoveCoordsDto moveCoordsDto = new MoveCoordsDto(1, 2);

        buildTestUser("test");
        var accessor = mockAccessor("test");

        webTestClient.post().uri("/gameMove")
                .header(SimpMessageHeaderAccessor.SESSION_ID_HEADER, accessor.getSessionId())
                .bodyValue(moveCoordsDto)
                .exchange()
                .expectStatus().isOk();
    }

    public User buildTestUser(String name) {
        var user = userFactory.createUser(name, "test");
        userDatabaseService.saveUser(user);
        return user;
    }

    public SimpMessageHeaderAccessor mockAccessor(String callerName) {
        var accessor = mock(SimpMessageHeaderAccessor.class);
        var principal = mock(Principal.class);
        when(principal.getName()).thenReturn(callerName);
        when(accessor.getUser()).thenReturn(principal);
        return accessor;
    }
}
