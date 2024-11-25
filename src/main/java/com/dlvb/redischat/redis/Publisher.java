package com.dlvb.redischat.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Publisher {

    @Value("${redis.uri}")
    private String uri;

    public void publish(String channel, String message) {
        StatefulRedisConnection<String, String> connection = RedisClient.create(uri).connect();
        log.info("Publishing the message to channel {}: {}", channel, message);
        connection.sync().publish(channel, message);
    }

}
