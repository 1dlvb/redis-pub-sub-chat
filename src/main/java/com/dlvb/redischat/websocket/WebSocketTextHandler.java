package com.dlvb.redischat.websocket;

import com.dlvb.redischat.redis.Publisher;
import com.dlvb.redischat.redis.Subscriber;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketTextHandler extends TextWebSocketHandler {

    @NonNull
    private WebSocketSessionManager webSocketSessionManager;

    @NonNull
    private Publisher redisPublisher;

    @NonNull
    private Subscriber redisSubscriber;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        webSocketSessionManager.addWebSocketSession(session);
        redisSubscriber.subscribe(WebSocketHelper.getUserIdFromSessionAttribute(session));
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        webSocketSessionManager.removeWebSocketSession(session);
        redisSubscriber.unsubscribe(WebSocketHelper.getUserIdFromSessionAttribute(session));
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String[] payload = message.getPayload().split("->");
        String targetUserId  = payload[0].trim();
        String messageToBeSent = payload[1].trim();
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        log.info("Got the payload {} and going to send to channel {}", payload, targetUserId);
        redisPublisher.publish(targetUserId, String.format("%s: %s ", userId, messageToBeSent));
    }
}