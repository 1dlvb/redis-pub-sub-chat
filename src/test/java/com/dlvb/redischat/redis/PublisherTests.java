package com.dlvb.redischat.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class PublisherTests {

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);;
    private Publisher publisher;

    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;

    @BeforeEach
    void setUp() {

        redisContainer.start();

        String redisUri = "redis://" + redisContainer.getHost() + ":" + redisContainer.getMappedPort(6379);
        redisClient = RedisClient.create(redisUri);
        pubSubConnection = redisClient.connectPubSub();

        publisher = new Publisher();
        setField(publisher, "uri", redisUri);
    }

    @AfterEach
    void tearDown() {
        pubSubConnection.close();
        redisClient.shutdown();
        redisContainer.stop();
    }

    @Test
    void testPublishMessageToRedisChannelPublishesMessageToTheTopic() {
        String channel = "test-channel";
        String expectedMessage = "test-msg";

        AtomicReference<String> receivedMessage = new AtomicReference<>();

        pubSubConnection.addListener(new RedisPubSubListener<>() {
            @Override
            public void message(String channel, String message) {
                receivedMessage.set(message);
            }

            @Override
            public void message(String pattern, String channel, String message) {}

            @Override
            public void subscribed(String channel, long count) {}

            @Override
            public void psubscribed(String pattern, long count) {}

            @Override
            public void unsubscribed(String channel, long count) {}

            @Override
            public void punsubscribed(String pattern, long count) {}
        });

        pubSubConnection.sync().subscribe(channel);
        publisher.publish(channel, expectedMessage);

        await().atMost(5, SECONDS).untilAsserted(() -> assertEquals(expectedMessage, receivedMessage.get()));
    }
}
