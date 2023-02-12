package com.rydzwr.tictactoe.web.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rydzwr.tictactoe.service.dto.incoming.InviteCodeDto;
import com.rydzwr.tictactoe.web.WebTestConfig;
import com.rydzwr.tictactoe.web.WebTestsHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Import(WebTestConfig.class)
@TestPropertySource(locations = "classpath:application.properties")
public class GameControllerTest {
    static final String TOKEN_PREFIX = "Bearer ";
    static final String LOGIN_PREFIX = "Basic ";
    static final String HEADER_STRING = HttpHeaders.AUTHORIZATION;
    private MockMvc mockMvc;

    @Autowired
    private WebTestsHelper testsHelper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private HttpHeaders getBasicAuthHeader(String name, String password) {
        String valueToEncode = name + ":" + password;
        String encodedValue = Base64.getEncoder().encodeToString(valueToEncode.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_STRING, LOGIN_PREFIX + encodedValue);
        return headers;
    }

    private HttpHeaders createBearerHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_STRING, TOKEN_PREFIX + token);
        return headers;
    }

    @Test
    @DisplayName("Should Create New Game For User And Return It As Json")
    public void createGameTest() throws Exception {

        var accessToken = testsHelper.createAndLoginTestUser(mockMvc, "test");
        assertNotNull(accessToken);

        var gameDto = testsHelper.buildGameDto(3, 3, 2, 0);
        var mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(gameDto);

        String expected = "{\"state\":\"IN_PROGRESS\",\"board\":\"---------\",\"currentPlayerMove\":\"X\",\"difficulty\":3,\"size\":3,\"playerPawn\":\"X\",\"awaitingPlayers\":1}";

        this.mockMvc.perform(
                        post("/api/game/createGame")
                                .servletPath("/api/game/createGame")
                                .headers(createBearerHeader(accessToken))
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(expected));
    }

    @Test
    @DisplayName("Should Return False When User Is Not In Game")
    public void testCanResumeGameEndpointFalseCase() throws Exception {

        var accessToken = testsHelper.createAndLoginTestUser(mockMvc, "test");
        assertNotNull(accessToken);

        this.mockMvc.perform(
                        get("/api/game/canResumeGame")
                                .servletPath("/api/game/canResumeGame")
                                .headers(createBearerHeader(accessToken)))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string("false"));
    }

    @Test
    @DisplayName("Should Return True When User Is In Game")
    public void testCanResumeGameEndpointTrueCase() throws Exception {

        var accessToken = testsHelper.createAndLoginTestUserWithLocalGame(mockMvc, "test", 3, 3, 2, 0);
        assertNotNull(accessToken);

        this.mockMvc.perform(
                        get("/api/game/canResumeGame")
                                .servletPath("/api/game/canResumeGame")
                                .headers(createBearerHeader(accessToken)))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string("true"));
    }

    @Test
    @DisplayName("Should Return Online Game Invite Code As 4-digit String")
    public void getInviteCodeTest() throws Exception {
        var accessToken = testsHelper.createAndLoginTestUserWithOnlineGame(mockMvc, "test", 3, 3, 1, 0);
        assertNotNull(accessToken);

        var result = this.mockMvc.perform(
                        get("/api/game/inviteCode")
                                .servletPath("/api/game/inviteCode")
                                .headers(createBearerHeader(accessToken)))
                .andExpect(status().is2xxSuccessful()).andReturn();

        var toTest = result.getResponse().getContentAsString();
        assertEquals(4, toTest.length());
    }

    @Test
    @DisplayName("Should Return Last Player Game Object As JSON")
    public void continueGameTest() throws Exception {

        var accessToken = testsHelper.createAndLoginTestUserWithLocalGame(mockMvc, "test", 3, 3, 2, 0);
        assertNotNull(accessToken);

        var expected = "{\"state\":\"IN_PROGRESS\",\"board\":\"---------\",\"currentPlayerMove\":\"X\",\"difficulty\":3,\"size\":3,\"playerPawn\":\"X\",\"awaitingPlayers\":null}";

        this.mockMvc.perform(
                        get("/api/game/continueGame")
                                .servletPath("/api/game/continueGame")
                                .headers(createBearerHeader(accessToken)))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string(expected));
    }

    @Test
    @DisplayName("Should Return Bad Request When Caller Is Not In Game")
    public void continueGameTestBadRequestCase() throws Exception {

        var accessToken = testsHelper.createAndLoginTestUser(mockMvc, "test");
        assertNotNull(accessToken);

        this.mockMvc.perform(
                        get("/api/game/continueGame")
                                .servletPath("/api/game/continueGame")
                                .headers(createBearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should Return Game Object As JSON After Player Joined")
    public void joinGameTest() throws Exception {

        // First user
        List<GrantedAuthority> authorities1 = new ArrayList<>();
        authorities1.add(new SimpleGrantedAuthority("USER"));

        Authentication auth1 = new UsernamePasswordAuthenticationToken("test", "test", authorities1);
        SecurityContextHolder.getContext().setAuthentication(auth1);

        var gameOwnerAccessToken = testsHelper.createAndLoginTestUserWithOnlineGame(mockMvc, auth1, "test", 3, 3, 2, 0);
        assertNotNull(gameOwnerAccessToken);

        SecurityContextHolder.clearContext();

        var expected = "{\"state\":\"IN_PROGRESS\",\"board\":\"---------\",\"currentPlayerMove\":\"X\",\"difficulty\":3,\"size\":3,\"playerPawn\":\"X\",\"awaitingPlayers\":null}";

        var inviteCode = testsHelper.getInviteCode(mockMvc, gameOwnerAccessToken);
        SecurityContextHolder.clearContext();

        var inviteCodeDto = new InviteCodeDto();
        inviteCodeDto.setInviteCode(inviteCode);

        var mapper = new ObjectMapper();
        var json = mapper.writeValueAsString(inviteCodeDto);

        SecurityContextHolder.clearContext();

        // Second user
        List<GrantedAuthority> authorities2 = new ArrayList<>();
        authorities2.add(new SimpleGrantedAuthority("USER"));

        Authentication auth2 = new UsernamePasswordAuthenticationToken("test2", "test", authorities2);
        SecurityContextHolder.getContext().setAuthentication(auth2);
        var secondPlayerAccessToken = testsHelper.createAndLoginTestUser(mockMvc, auth2, "test2");

        SecurityContextHolder.clearContext();

        var auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test2");
        SecurityContextHolder.getContext().setAuthentication(auth);

        var returned = this.mockMvc.perform(
                        post("/api/game/joinGame")
                                .servletPath("/api/game/joinGame")
                                .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                                .headers(createBearerHeader(secondPlayerAccessToken))
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(expected)).andReturn();

        log.info("RETURNED: --> {}", returned.getResponse().getContentAsString());
        log.info("RETURNED: --> {}", returned.getResponse().getErrorMessage());
        log.info("RETURNED: --> {}", returned.getResponse());
    }
}