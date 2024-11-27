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


/**
 * WebSocketTextHandler handles WebSocket communication for chat messages.
 * It is responsible for managing user connections, caching chat messages,
 * and publishing and subscribing to Redis channels for message delivery.
 * @author Matushkin Anton
 */
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

    /**
     * Invoked when a WebSocket connection is established.
     * It adds the WebSocket session, subscribes the user to their channel,
     * and sends any cached messages to the user.
     *
     * @param session The WebSocket session.
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        webSocketSessionManager.addWebSocketSession(session);
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        redisSubscriber.subscribe(userId);

        sendCachedMessages(session, userId);
    }

    /**
     * Invoked when a WebSocket connection is closed.
     * It removes the WebSocket session and unsubscribes the user from their channel.
     *
     * @param session The WebSocket session.
     * @param status The status of the connection closure.
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        webSocketSessionManager.removeWebSocketSession(session);
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        redisSubscriber.unsubscribe(userId);
    }

    /**
     * Handles incoming text messages.
     * The message format is expected to be "targetUserId -> message".
     * The message is cached for both the sender and the target user,
     * and published to the target user's Redis channel.
     *
     * @param session The WebSocket session.
     * @param message The incoming message.
     */
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

    /**
     * Caches a message for a user in Redis.
     *
     * @param userId The user ID associated with the message.
     * @param message The message to be cached.
     */
    private void cacheMessage(String userId, String message) {
        String redisKey = "chat:history:" + userId;

        long timestamp = System.currentTimeMillis();
        String messageWithTimestamp = timestamp + "|" + message;

        redisTemplate.opsForList().rightPush(redisKey, messageWithTimestamp);
        redisTemplate.opsForList().trim(redisKey, 0, 19);
    }

    /**
     * Sends cached messages to the user when they first connect.
     * It retrieves the last 20 messages from Redis and sends them in order.
     *
     * @param session The WebSocket session of the user.
     * @param userId The user ID associated with the messages.
     */
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
