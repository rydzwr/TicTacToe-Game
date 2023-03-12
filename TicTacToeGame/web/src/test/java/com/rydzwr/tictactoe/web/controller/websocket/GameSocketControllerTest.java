package com.rydzwr.tictactoe.web.controller.websocket;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.GameResultDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.web.WebTestConfig;
import com.rydzwr.tictactoe.web.WebTestsHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.TestPropertySource;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Slf4j
@AutoConfigureMockMvc
@Import(WebTestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class GameSocketControllerTest {
    @Autowired
    private UserRepository userRepository;
    private MockMvc mockMvc;
    private WebSocketStompClient stompClient;
    @Autowired
    private WebTestsHelper testsHelper;
    @Autowired
    private WebApplicationContext context;
    public static final String WEB_SOCKET_TOPIC_GAME_MOVE_ENDPOINT = "/app/gameMove";
    public final String WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT = "/topic/gameBoard";
    public final String WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT = "/topic/gameState";
    private CompletableFuture<PlayerMoveResponseDto> gameBoardTopic$;
    private CompletableFuture<GameStateDto> gameStateTopic$;
    private CompletableFuture<Integer> awaitingPlayersTopic$;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        this.gameBoardTopic$ = new CompletableFuture<>();
        this.gameStateTopic$ = new CompletableFuture<>();
        this.awaitingPlayersTopic$ = new CompletableFuture<>();

        this.stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    @DisplayName("Should Process Two Player Moves")
    public void testSendTwoPlayerMoves() throws Exception {
        final String GAME_SOCKET_CONTROLLER_TEST_ONE = "socketsUserOne";

        var stompSession = initWSConnection(GAME_SOCKET_CONTROLLER_TEST_ONE);
        var moveCoordsDto = new MoveCoordsDto(0, 0);

        // SUBSCRIBING TO SUBJECT (CompletableFuture)
        stompSession.subscribe(WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardTopicFrameHandler());

        // SENDING PLAYER MOVE TO ENDPOINT
        stompSession.send(WEB_SOCKET_TOPIC_GAME_MOVE_ENDPOINT, moveCoordsDto);

        // PULLING RESPONSE FROM SUBJECT (CompletableFuture)
        var firstReceivedGameState = gameBoardTopic$.get(1, SECONDS);

        // THEN
        assertAll("game state",
                () -> assertNotNull(firstReceivedGameState),
                () -> assertArrayEquals(new Character[]{'X'}, firstReceivedGameState.getProcessedMovesPawns().toArray())
        );

        // SAVING UP PAWN BEFORE UPDATING GAME FOR LATER VALIDATION
        var nextPlayerPawn = firstReceivedGameState.getCurrentPlayerMove();

        // SECOND PLAYER MOVE VALUES
        var moveCoordsDto2 = new MoveCoordsDto(1, 1);

        // OVERRIDING SUBJECT BECAUSE IT'S JAVA NOT RXJS
        gameBoardTopic$ = new CompletableFuture<>();

        // SENDING SECOND PLAYER MOVE
        stompSession.send(WEB_SOCKET_TOPIC_GAME_MOVE_ENDPOINT, moveCoordsDto2);

        // PULLING RESPONSE FROM SUBJECT (CompletableFuture)
        var secondReceivedGameState = gameBoardTopic$.get(1, SECONDS);

        // THEN
        assertAll("game state",
                () -> assertNotNull(secondReceivedGameState),
                () -> assertArrayEquals(new Character[]{nextPlayerPawn}, secondReceivedGameState.getProcessedMovesPawns().toArray())
        );
    }

    @Test
    @DisplayName("Should Return WIN State And Winner Pawn")
    public void testSendReturnsPlayerWin() throws Exception {
        final String GAME_SOCKET_CONTROLLER_TEST_TWO = "socketsUserTwo";
        var stompSession = initWSConnection(GAME_SOCKET_CONTROLLER_TEST_TWO);

        stompSession.subscribe(WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardTopicFrameHandler());
        stompSession.subscribe(WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateTopicFrameHandler());

        movePlayer(stompSession, 0,0);
        movePlayer(stompSession, 2,0);
        movePlayer(stompSession, 0,1);
        movePlayer(stompSession, 2,1);

        var moveCoordsDto = new MoveCoordsDto(0, 2);
        stompSession.send(WEB_SOCKET_TOPIC_GAME_MOVE_ENDPOINT, moveCoordsDto);

        var toTest = gameStateTopic$.get(5, SECONDS);

        // THEN
        assertAll("game state",
                () -> assertNotNull(toTest),
                //() -> assertEquals('X', toTest.getGameResult().getWinnerPawn().charValue()),
                () -> assertEquals("FINISHED", toTest.getGameState()),
                () -> assertEquals("WIN", toTest.getGameResult().getResult())
        );
    }

    @Test
    @DisplayName("Should Return DRAW State")
    public void testSendReturnsDraw() throws Exception {
        final String GAME_SOCKET_CONTROLLER_TEST_THREE = "socketsUserThree";
        var stompSession = initWSConnection(GAME_SOCKET_CONTROLLER_TEST_THREE);

        stompSession.subscribe(WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardTopicFrameHandler());
        stompSession.subscribe(WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT, new GameStateTopicFrameHandler());


        movePlayer(stompSession, 0,0);
        movePlayer(stompSession, 1,1);
        movePlayer(stompSession, 0,1);
        movePlayer(stompSession, 2,1);
        movePlayer(stompSession, 1,2);
        movePlayer(stompSession, 0,2);
        movePlayer(stompSession, 2,0);
        movePlayer(stompSession, 1,0);

        var moveCoordsDto = new MoveCoordsDto(2, 2);
        stompSession.send(WEB_SOCKET_TOPIC_GAME_MOVE_ENDPOINT, moveCoordsDto);             // PLAYER ONE

        var toTest = gameStateTopic$.get(5, SECONDS);

        // THEN
        assertAll("game state",
                () -> assertNotNull(toTest),
                () -> assertEquals("FINISHED", toTest.getGameState()),
                () -> assertEquals("DRAW", toTest.getGameResult().getResult())
        );
    }

    private StompSession initWSConnection(String userName) throws Exception {
        var accessToken = testsHelper.createAndLoginTestUserWithLocalGame(mockMvc, userName, 3, 3, 1, 0);
        String SOCKET_URL = "ws://localhost:5000/game?token=";
        final String URL = SOCKET_URL + accessToken;

        var stompSession = stompClient.connectAsync(URL, createWebSocketHeader(accessToken), new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        return stompSession;
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class GameBoardTopicFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return PlayerMoveResponseDto.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            gameBoardTopic$.complete((PlayerMoveResponseDto) o);
        }
    }

    private class GameStateTopicFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Object.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();

                module.addDeserializer(GameStateDto.class, new GameStateDeserializer());
                mapper.registerModule(module);

                var value = mapper.readValue((byte[]) o, GameStateDto.class);

                gameStateTopic$.complete(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class GameStateDeserializer extends StdDeserializer<GameStateDto> {

        public GameStateDeserializer() {
            this(null);
        }

        public GameStateDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public GameStateDto deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {

            JsonNode gameStateDtoNode = jp.getCodec().readTree(jp);

            var gameStateDto = new GameStateDto(gameStateDtoNode.get("gameState").textValue());

            var gameResult = new GameResultDto(
                    gameStateDtoNode.get("gameResult").get("result").textValue(), null
                    //gameStateDtoNode.get("gameResult").get("winnerPawn").textValue().charAt(0)
            );

            gameStateDto.setGameResult(gameResult);

            return gameStateDto;
        }
    }

    private class AwaitingPlayersTopicFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Integer.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            awaitingPlayersTopic$.complete((Integer) o);
        }
    }

    private WebSocketHttpHeaders createWebSocketHeader(String token) {
        final String TOKEN_PREFIX = "Bearer ";
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", TOKEN_PREFIX + token);
        return headers;
    }

    private void movePlayer(StompSession stompSession, int x, int y) throws ExecutionException, InterruptedException, TimeoutException {
        gameBoardTopic$ = new CompletableFuture<>();
        var moveCoordsDto = new MoveCoordsDto(x, y);
        stompSession.send(WEB_SOCKET_TOPIC_GAME_MOVE_ENDPOINT, moveCoordsDto);
        gameBoardTopic$.get(1, SECONDS);
    }

}
