package com.dlvb.redischat.websocket;

import com.dlvb.redischat.redis.Publisher;
import com.dlvb.redischat.redis.Subscriber;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Comparator;
import java.util.List;

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

    @NonNull
    private StringRedisTemplate redisTemplate;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        webSocketSessionManager.addWebSocketSession(session);
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        redisSubscriber.subscribe(userId);

        sendCachedMessages(session, userId);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        webSocketSessionManager.removeWebSocketSession(session);
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        redisSubscriber.unsubscribe(userId);
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String[] payload = message.getPayload().split("->");
        String targetUserId = payload[0].trim();
        String messageToBeSent = payload[1].trim();
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);

        log.info("Got the payload {}. Sending to channel {}", payload, targetUserId);

        cacheMessage(userId, String.format("%s: %s", userId, messageToBeSent));
        cacheMessage(targetUserId, String.format("%s: %s", userId, messageToBeSent));

        redisPublisher.publish(targetUserId, String.format("%s: %s", userId, messageToBeSent));
    }

    private void cacheMessage(String userId, String message) {
        String redisKey = "chat:history:" + userId;

        long timestamp = System.currentTimeMillis();
        String messageWithTimestamp = timestamp + "|" + message;

        redisTemplate.opsForList().rightPush(redisKey, messageWithTimestamp);
        redisTemplate.opsForList().trim(redisKey, 0, 19);
    }

    private void sendCachedMessages(WebSocketSession session, String userId) {

        String redisKey = "chat:history:" + userId;

        List<String> messages = redisTemplate.opsForList().range(redisKey, 0, -1);

        if (messages != null && !messages.isEmpty()) {
            messages.stream()
                    .filter(msg -> msg.contains("|"))
                    .sorted(Comparator.comparingLong(msg -> {
                        try {
                            return Long.parseLong(msg.split("\\|")[0]);
                        } catch (NumberFormatException e) {
                            log.warn("Invalid message format: {}", msg, e);
                            return Long.MIN_VALUE;
                        }
                    }))
                    .map(msg -> msg.split("\\|", 2)[1])
                    .forEach(msg -> {
                        try {
                            session.sendMessage(new TextMessage(msg));
                        } catch (Exception e) {
                            log.error("Error sending cached message: {}", msg, e);
                        }
                    });
        }
    }

}
