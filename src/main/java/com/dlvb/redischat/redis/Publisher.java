package com.dlvb.redischat.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class that responsible for publishing messages to a specific Redis channel.
 * @author Matushkin Anton
 */
@Slf4j
@Component
public class Publisher {

    @Value("${redis.uri}")
    private String uri;

    /**
     * Publishes a message to a specified Redis channel.
     *
     * @param channel the Redis channel to which the message will be published.
     * @param message the message to be sent to the channel.
     */
    public void publish(String channel, String message) {
        StatefulRedisConnection<String, String> connection = RedisClient.create(uri).connect();
        log.info("Publishing the message to channel {}: {}", channel, message);
        connection.sync().publish(channel, message);
    }

}
