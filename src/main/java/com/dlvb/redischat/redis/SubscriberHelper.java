package com.dlvb.redischat.redis;

import com.dlvb.redischat.websocket.WebSocketSessionManager;
import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * SubscriberHelper listens for messages from Redis Pub/Sub channels and forwards the messages
 * to the corresponding WebSocket sessions.
 * @author Matushkin Anton
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriberHelper implements RedisPubSubListener<String, String> {

    @NonNull
    private WebSocketSessionManager webSocketSessionManager;

    /**
     * This method is called when a message is received from a Redis Pub/Sub channel.
     *
     * @param channel The Redis channel where the message was received from.
     * @param message The message received from the Redis channel.
     */
    @Override
    public void message(String channel, String message) {
        log.info("Got the message on redis {}: {}", channel, message);
        WebSocketSession ws = webSocketSessionManager.getWebSocketSessions(channel);
        try {
            ws.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("Error while sending message to the channel ({}) ", channel);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void message(String s, String k1, String s2) {

    }

    @Override
    public void subscribed(String s, long l) {

    }

    @Override
    public void psubscribed(String s, long l) {

    }

    @Override
    public void unsubscribed(String s, long l) {

    }

    @Override
    public void punsubscribed(String s, long l) {

    }

}
