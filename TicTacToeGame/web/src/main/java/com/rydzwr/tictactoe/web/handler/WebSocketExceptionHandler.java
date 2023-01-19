package com.rydzwr.tictactoe.web.handler;

import com.rydzwr.tictactoe.game.constants.GameConstants;
import com.rydzwr.tictactoe.game.exception.ExceptionModel;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketExceptionHandler {
    public void sendException(SimpMessagingTemplate template, String message) {
        template.convertAndSend(
                message,
                 new ExceptionModel(message)
        );
        throw new IllegalArgumentException(message);
    }
}
