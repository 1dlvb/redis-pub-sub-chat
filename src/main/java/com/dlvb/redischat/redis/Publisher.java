package com.dlvb.redischat.redis;

import io.lettuce.core.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Publisher {

    @Value("${redis.uri}")
    private String uri;
    private final RedisClient client = RedisClient.create(uri);


    public void publish(String channel, String message){
        log.info("Going to publish the message to channel {} and message = {}", channel, message);
        var connection = this.client.connect();
        connection.sync().publish(channel,message);
    }
}