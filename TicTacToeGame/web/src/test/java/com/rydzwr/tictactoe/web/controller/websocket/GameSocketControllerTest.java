package com.rydzwr.tictactoe.web.controller.websocket;

import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.web.WebTestConfig;
import com.rydzwr.tictactoe.web.WebTestsHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.TestPropertySource;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Import(WebTestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class GameSocketControllerTest {
    static final String TOKEN_PREFIX = "Bearer ";
    static final String HEADER_STRING = AUTHORIZATION;
    @Autowired
    private GameSocketController gameSocketController;
    @Autowired
    private WebTestsHelper webTestsHelper;

    private MockMvc mockMvc;

    @Autowired
    private WebTestsHelper testsHelper;

    @Autowired
    private WebApplicationContext context;

    private int port;
    private String URL;

    public static final String WEB_SOCKET_TOPIC_GAME_MOVE_ENDPOINT = "/app/gameMove";
    public static final String WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT = "/topic/gameBoard";
    public static final String WEB_SOCKET_TOPIC_GAME_STATE_ENDPOINT = "/topic/gameState";
    public static final String WEB_SOCKET_AWAITING_PLAYERS_ENDPOINT = "/topic/awaitingPlayers";

    private CompletableFuture<PlayerMoveResponseDto> gameBoardTopic$ = new CompletableFuture<>();
    private CompletableFuture<GameStateDto> gameStateTopic$ = new CompletableFuture<>();
    private CompletableFuture<Integer> awaitingPlayersTopic$ = new CompletableFuture<>();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public CompletableFuture<String> calculateAsync() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(5000);
            completableFuture.complete("Hello");
            return null;
        });

        return completableFuture;
    }

    @Test
    public void testSend() throws Exception {

        /*
        CompletableFuture<String> myPromise = calculateAsync();
        log.info("SYNC CALL");
        //   String result = myPromise.get(); // BLOCKS CURRENT THREAD UNTIL VALUE EMITTED
        //   log.info("RESULT: --> {}", result);
        myPromise.thenApply(val -> val + " World!").thenAccept(System.out::println);
        myPromise.get();

         */

        var accessToken = webTestsHelper.createAndLoginTestUserWithLocalGame(mockMvc, "test", 3, 3, 2, 0);
        var SOCKET_URL = "ws://localhost:5000/game?token=" + accessToken;

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        StompSession stompSession = stompClient.connectAsync(SOCKET_URL, headers, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(WEB_SOCKET_TOPIC_GAME_BOARD_ENDPOINT, new GameBoardTopicFrameHandler());
        stompSession.send(WEB_SOCKET_TOPIC_GAME_MOVE_ENDPOINT, moveCoordsDto);

        var gameState = gameBoardTopic$.get(1, SECONDS);

        log.info("PROCESSED PAWNS: --> {}", gameState.getProcessedMovesPawns());

        assertNotNull(gameState);
        assertArrayEquals(new Character[] { 'X' },gameState.getProcessedMovesPawns().toArray());
        var nextPlayerPawn = gameState.getCurrentPlayerMove();

        var moveCoordsDto2 = new MoveCoordsDto();
        moveCoordsDto2.setX(1);
        moveCoordsDto2.setY(1);

        gameBoardTopic$ = new CompletableFuture<>();
        stompSession.send(WEB_SOCKET_TOPIC_GAME_MOVE_ENDPOINT, moveCoordsDto2);

        var gameState2 = gameBoardTopic$.get(1, SECONDS);

        assertNotNull(gameState2);
        assertArrayEquals(new Character[] { nextPlayerPawn }, gameState2.getProcessedMovesPawns().toArray());
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

    private class GameStateTopicFrameHandlerFor implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return GameStateDto.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            gameStateTopic$.complete((GameStateDto) o);
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

    private HttpHeaders createBearerHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_STRING, TOKEN_PREFIX + token);
        return headers;
    }
}
