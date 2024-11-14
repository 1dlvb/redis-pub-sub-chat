package com.dlvb.redischat.redis;

import io.lettuce.core.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Publisher {

    private final RedisClient client = RedisClient.create("redis://localhost:6379");


    public void publish(String channel, String message){
        log.info("Going to publish the message to channel {} and message = {}", channel, message);
        var connection = this.client.connect();
        connection.sync().publish(channel,message);
    }
}