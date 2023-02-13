package com.rydzwr.tictactoe.web.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.service.dto.incoming.InviteCodeDto;
import com.rydzwr.tictactoe.service.dto.outgoing.LoadGameDto;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Import(WebTestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
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

    private HttpHeaders createBearerHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_STRING, TOKEN_PREFIX + token);
        return headers;
    }

    @Test
    @DisplayName("Should Create New Game For User And Return It As Json")
    public void createGameTest() throws Exception {
        final String GAME_CONTROLLER_TEST_ONE = "TestOne";
        var accessToken = testsHelper.createAndLoginTestUser(mockMvc, GAME_CONTROLLER_TEST_ONE);
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
        final String GAME_CONTROLLER_TEST_TWO = "TestTwo";
        var accessToken = testsHelper.createAndLoginTestUser(mockMvc, GAME_CONTROLLER_TEST_TWO);
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
        final String GAME_CONTROLLER_TEST_THREE = "TestThree";
        var accessToken = testsHelper.createAndLoginTestUserWithLocalGame(mockMvc, GAME_CONTROLLER_TEST_THREE, 3, 3, 2, 0);
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
        final String GAME_CONTROLLER_TEST_FOUR = "TestFour";
        var accessToken = testsHelper.createAndLoginTestUserWithOnlineGame(mockMvc, GAME_CONTROLLER_TEST_FOUR, 3, 3, 1, 0);
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
        final String GAME_CONTROLLER_TEST_FIVE = "TestFive";
        var accessToken = testsHelper.createAndLoginTestUserWithLocalGame(mockMvc, GAME_CONTROLLER_TEST_FIVE, 3, 3, 2, 0);
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
        final String GAME_CONTROLLER_TEST_SIX = "TestSix";
        var accessToken = testsHelper.createAndLoginTestUser(mockMvc, GAME_CONTROLLER_TEST_SIX);
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
        final String GAME_CONTROLLER_TEST_SEVEN = "TestSeven";
        var gameOwnerAccessToken = testsHelper.createAndLoginTestUserWithOnlineGame(mockMvc, GAME_CONTROLLER_TEST_SEVEN, 3, 3, 2, 0);
        var inviteCode = testsHelper.getInviteCode(mockMvc, gameOwnerAccessToken);

        var inviteCodeDto = new InviteCodeDto();
        inviteCodeDto.setInviteCode(inviteCode);

        var mapper = new ObjectMapper();
        var json = mapper.writeValueAsString(inviteCodeDto);

        final String GAME_CONTROLLER_TEST_EIGHT = "TestEight";
        var secondPlayerAccessToken = testsHelper.createAndLoginTestUser(this.mockMvc, GAME_CONTROLLER_TEST_EIGHT);

        var returned = this.mockMvc.perform(
                        post("/api/game/joinGame")
                                .servletPath("/api/game/joinGame")
                                .headers(createBearerHeader(secondPlayerAccessToken))
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        this.mockMvc.perform(
                        get("/api/game/canResumeGame")
                                .servletPath("/api/game/canResumeGame")
                                .headers(createBearerHeader(secondPlayerAccessToken)))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string("true"));

        var expectedBoard = "---------";
        var expectedState = GameState.IN_PROGRESS.name();
        var expectedDifficulty = 3;
        var expectedAwaitingPlayersCount = 0;
        var expectedGameSize = 3;

        var returnedString = returned.getResponse().getContentAsString();
        var asJSON = mapper.readValue(returnedString, LoadGameDto.class);

        assertEquals(expectedBoard, asJSON.getBoard());
        assertEquals(expectedState, asJSON.getState());
        assertEquals(expectedDifficulty, asJSON.getDifficulty());
        assertEquals(expectedAwaitingPlayersCount, asJSON.getAwaitingPlayers());
        assertEquals(expectedGameSize, asJSON.getSize());
    }
}