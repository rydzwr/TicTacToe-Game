package com.rydzwr.tictactoe.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import com.rydzwr.tictactoe.service.game.database.PlayerDatabaseService;
import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import com.rydzwr.tictactoe.service.security.factory.UserFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
@Import(WebTestConfig.class)
public class WebTestsHelper {
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private UserDatabaseService userDatabaseService;

    @Autowired
    private GameDatabaseService gameDatabaseService;

    @Autowired
    private GameBuilderService gameBuilderService;
    @Autowired
    private PlayerDatabaseService playerDatabaseService;

    static final String TOKEN_PREFIX = "Bearer ";
    static final String LOGIN_PREFIX = "Basic ";
    static final String HEADER_STRING = HttpHeaders.AUTHORIZATION;

    public User buildTestUser(String name) {
        var user = userFactory.createUser(name, "test");
        userDatabaseService.saveUser(user);
        return user;
    }

    public GameDto buildGameDto(int gameSize, int gameDifficulty, int localPlayersCount, int aiPlayersCount) {
        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < localPlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.LOCAL.name());
            playerDtoList.add(playerDto);
        }

        for (int i = 0; i < aiPlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.AI.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();

        gameDto.setGameSize(gameSize);
        gameDto.setGameDifficulty(gameDifficulty);
        gameDto.setPlayers(playerDtoList);
        return gameDto;
    }

    public String createAndLoginTestUser(MockMvc mockMvc, String userName) throws Exception {
        final String[] results = new String[2];

        String passwordValue = "test123";
        final String requestBody = "{\"name\":\"" + userName + "\",\"password\":\"" + passwordValue + "\"}";

        // REGISTERING NEW USER TO DB
        mockMvc.perform(
                        post("/api/user/register")
                                .servletPath("/api/user/register")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        // LOGGING AFTER SUCCESSFUL REGISTER
        mockMvc.perform(
                        get("/api/login")
                                .servletPath("/api/login")
                                .headers(getBasicAuthHeader("test", "test123")))
                .andDo(result -> {
                    var parser = new JSONObject(result.getResponse().getContentAsString());
                    results[0] = parser.getString("access_token");
                    results[1] = parser.getString("role");
                });

        return results[0];
    }

    public String createAndLoginTestUser(MockMvc mockMvc, Authentication auth, String userName) throws Exception {
        final String[] results = new String[2];

        String passwordValue = "test123";
        final String requestBody = "{\"name\":\"" + userName + "\",\"password\":\"" + passwordValue + "\"}";

        // REGISTERING NEW USER TO DB
        mockMvc.perform(
                        post("/api/user/register")
                                .servletPath("/api/user/register")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        // LOGGING AFTER SUCCESSFUL REGISTER
        mockMvc.perform(
                        get("/api/login")
                                .servletPath("/api/login")
                                .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                .headers(getBasicAuthHeader("test", "test123")))
                .andDo(result -> {
                    var parser = new JSONObject(result.getResponse().getContentAsString());
                    results[0] = parser.getString("access_token");
                    results[1] = parser.getString("role");
                });

        return results[0];
    }

    public String createAndLoginTestUserWithOnlineGame(MockMvc mockMvc, String userName, int gameSize, int gameDifficulty, int onlinePlayersCount, int aiPlayersCount) throws Exception {
        var accessToken = createAndLoginTestUser(mockMvc, userName);

        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < onlinePlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.ONLINE.name());
            playerDtoList.add(playerDto);
        }

        for (int i = 0; i < aiPlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.AI.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();

        gameDto.setGameSize(gameSize);
        gameDto.setGameDifficulty(gameDifficulty);
        gameDto.setPlayers(playerDtoList);

        var mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(gameDto);

        mockMvc.perform(
                        post("/api/game/createGame")
                                .servletPath("/api/game/createGame")
                                .headers(createBearerHeader(accessToken))
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        return accessToken;
    }

    public String createAndLoginTestUserWithOnlineGame(MockMvc mockMvc, Authentication auth, String userName, int gameSize, int gameDifficulty, int onlinePlayersCount, int aiPlayersCount) throws Exception {
        var accessToken = createAndLoginTestUser(mockMvc, userName);

        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < onlinePlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.ONLINE.name());
            playerDtoList.add(playerDto);
        }

        for (int i = 0; i < aiPlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.AI.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();

        gameDto.setGameSize(gameSize);
        gameDto.setGameDifficulty(gameDifficulty);
        gameDto.setPlayers(playerDtoList);

        var mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(gameDto);

        mockMvc.perform(
                        post("/api/game/createGame")
                                .servletPath("/api/game/createGame")
                                .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                                .headers(createBearerHeader(accessToken))
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        return accessToken;
    }

    public String createAndLoginTestUserWithLocalGame(MockMvc mockMvc, String userName, int gameSize, int gameDifficulty, int localPlayersCount, int aiPlayersCount) throws Exception {
        var accessToken = createAndLoginTestUser(mockMvc, userName);

        var gameDto = buildGameDto(gameSize, gameDifficulty, localPlayersCount, aiPlayersCount);

        var mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(gameDto);

        mockMvc.perform(
                        post("/api/game/createGame")
                                .servletPath("/api/game/createGame")
                                .headers(createBearerHeader(accessToken))
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        return accessToken;
    }

    public String getInviteCode(MockMvc mockMvc, String accessToken) throws Exception {
        var result = mockMvc.perform(
                        get("/api/game/inviteCode")
                                .servletPath("/api/game/inviteCode")
                                .headers(createBearerHeader(accessToken)))
                .andExpect(status().is2xxSuccessful()).andReturn();

        return result.getResponse().getContentAsString();
    }

    private HttpHeaders getBasicAuthHeader(String name, String password) {
        String valueToEncode = name + ":" + password;
        String encodedValue = Base64.getEncoder().encodeToString(valueToEncode.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_STRING,LOGIN_PREFIX + encodedValue);
        return headers;
    }

    private HttpHeaders createBearerHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_STRING, TOKEN_PREFIX + token);
        return headers;
    }

}
