package com.dlvb.redischat.redis;

import com.dlvb.redischat.websocket.WebSocketSessionManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Testcontainers
class SubscriberTests {

    @Container
    private static final GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:latest").withExposedPorts(6379);

    private Subscriber subscriber;
    private WebSocketSessionManager sessionManager;
    private RedisClient redisClient;
    private String testChannel;

    @BeforeEach
    void setUp() {
        String redisUri = "redis://" + redisContainer.getHost() + ":" + redisContainer.getFirstMappedPort();
        sessionManager = mock(WebSocketSessionManager.class);
        redisClient = RedisClient.create(redisUri);
        subscriber = new Subscriber(redisUri, sessionManager);
        testChannel = "test-channel";
        subscriber.subscribe(testChannel);

    }

    @AfterEach
    void tearDown() {
        subscriber.unsubscribe(testChannel);
    }

    @Test
    void testSubscribeReturnsReceivedMessage() throws IOException {
        String testMessage = "test-message";
        RedisPubSubCommands<String, String> publisher = redisClient.connectPubSub().sync();
        WebSocketSession mockSession = mock(WebSocketSession.class);

        when(sessionManager.getWebSocketSessions(testChannel)).thenReturn(mockSession);

        subscriber.subscribe(testChannel);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> publisher.publish(testChannel, testMessage));

        verify(mockSession).sendMessage(any(TextMessage.class));

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(mockSession).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        assertEquals(testMessage, capturedMessage.getPayload());
    }


    @Test
    void testUnsubscribeReturnsNothing() throws IOException {
        String testMessage = "test-message-not-received";
        RedisPubSubCommands<String, String> publisher = redisClient.connectPubSub().sync();
        WebSocketSession mockSession = mock(WebSocketSession.class);

        when(sessionManager.getWebSocketSessions(testChannel)).thenReturn(mockSession);

        subscriber.subscribe(testChannel);
        subscriber.unsubscribe(testChannel);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> publisher.publish(testChannel, testMessage));

        verify(mockSession, never()).sendMessage(any(TextMessage.class));
    }


}
