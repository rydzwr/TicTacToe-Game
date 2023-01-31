package com.rydzwr.tictactoe.web.handler;

import com.rydzwr.tictactoe.web.exception.ExceptionModel;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketExceptionHandler {
    private final SimpMessagingTemplate template;
    public void sendException(String message) {
        template.convertAndSend(
                WebConstants.WEB_SOCKET_TOPIC_ERROR_ENDPOINT,
                 new ExceptionModel(message)
        );
        throw new IllegalArgumentException(message);
    }
}
