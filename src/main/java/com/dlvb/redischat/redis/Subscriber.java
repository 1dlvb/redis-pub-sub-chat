package com.dlvb.redischat.redis;

import com.dlvb.redischat.websocket.WebSocketSessionManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Subscriber class is responsible for subscribing and unsubscribing to Redis channels
 * @author Matushkin Anton
 */
@Component
public class Subscriber {

    private final RedisPubSubCommands<String, String> sync;

    /**
     * Constructor that initializes a Redis Pub/Sub connection and sets up the listener.
     *
     * @param uri The URI of the Redis server.
     * @param webSocketSessionManager The WebSocketSessionManager used to manage WebSocket sessions.
     */
    public Subscriber(@Value("${redis.uri}") String uri, WebSocketSessionManager webSocketSessionManager) {
        StatefulRedisPubSubConnection<String, String> connection = RedisClient.create(uri).connectPubSub();
        SubscriberHelper redisListener = new SubscriberHelper(webSocketSessionManager);
        connection.addListener(redisListener);
        this.sync = connection.sync();
    }

    /**
     * Subscribes to a specified Redis channel.
     *
     * @param channel The Redis channel to subscribe to.
     */
    public void subscribe(String channel) {
        sync.subscribe(channel);
    }

    /**
     * Unsubscribes from a specified Redis channel.
     *
     * @param channel The Redis channel to unsubscribe from.
     */
    public void unsubscribe(String channel) {
        sync.unsubscribe(channel);
    }

}
