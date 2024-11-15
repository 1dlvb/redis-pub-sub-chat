package com.dlvb.redischat.redis;

import com.dlvb.redischat.websocket.WebSocketSessionManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Subscriber {

    @Value("${redis.uri}")
    private String uri;
    private final RedisPubSubCommands<String, String> sync;

    public Subscriber(WebSocketSessionManager webSocketSessionManager){
        RedisClient client = RedisClient.create(uri);
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        var redisListener = new SubscriberHelper(webSocketSessionManager);
        connection.addListener(redisListener);
        this.sync = connection.sync();
    }

    public void subscribe(String channel){
        sync.subscribe(channel);
    }

    public void  unsubscribe(String channel){
        sync.unsubscribe(channel);
    }
}