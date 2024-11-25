package com.dlvb.redischat.websocket;

import com.dlvb.redischat.redis.Publisher;
import com.dlvb.redischat.redis.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketTextHandlerTests {

    @Mock
    private WebSocketSessionManager webSocketSessionManager;

    @Mock
    private Publisher redisPublisher;

    @Mock
    private Subscriber redisSubscriber;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private WebSocketTextHandler webSocketTextHandler;

    @BeforeEach
    void setUp() {
        ListOperations<String, String> listOpsMock = mock(ListOperations.class);
        when(redisTemplate.opsForList()).thenReturn(listOpsMock);
    }

    @Test
    void testAfterConnectionEstablishedSendsCachedMessages() {
        String userId = "user123";

        when(session.getAttributes()).thenReturn(Map.of("userId", userId));

        webSocketTextHandler.afterConnectionEstablished(session);

        verify(webSocketSessionManager).addWebSocketSession(session);
        verify(redisSubscriber).subscribe(userId);

        verify(redisTemplate).opsForList();
    }

    @Test
    void testHandleTextMessageHandlesIncomingTextMessages() {
        String userId = "user123";
        String targetUserId = "user321";
        String messageToBeSent = "test";
        String payload = targetUserId + "->" + messageToBeSent;

        when(session.getAttributes()).thenReturn(Map.of("userId", userId));

        ListOperations<String, String> listOpsMock = mock(ListOperations.class);
        when(redisTemplate.opsForList()).thenReturn(listOpsMock);

        when(listOpsMock.rightPush(anyString(), anyString())).thenReturn(1L);

        webSocketTextHandler.handleTextMessage(session, new TextMessage(payload));

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(listOpsMock, times(2)).rightPush(keyCaptor.capture(), valueCaptor.capture());

        verify(redisPublisher).publish(eq(targetUserId), eq(userId + ": " + messageToBeSent));

        List<String> capturedKeys = keyCaptor.getAllValues();
        List<String> capturedValues = valueCaptor.getAllValues();

        assertTrue(capturedKeys.stream().anyMatch(key -> key.equals("chat:history:" + userId)));
        assertTrue(capturedKeys.stream().anyMatch(key -> key.equals("chat:history:" + targetUserId)));
        assertTrue(capturedValues.stream().anyMatch(value -> value.contains(userId + ": " + messageToBeSent)));
    }



    @Test
    void testSendCachedMessagesActuallySendsCachedMessages() throws Exception {
        String userId = "user123";
        String redisKey = "chat:history:" + userId;
        List<String> mockMessages = List.of("1|user123: test1", "2|user123: test2");

        when(redisTemplate.opsForList().range(redisKey, 0, -1)).thenReturn(mockMessages);

        Method sendCachedMessagesMethod = WebSocketTextHandler.class.getDeclaredMethod("sendCachedMessages",
                WebSocketSession.class, String.class);
        sendCachedMessagesMethod.setAccessible(true);

        sendCachedMessagesMethod.invoke(webSocketTextHandler, session, userId);

        verify(session, times(2)).sendMessage(any(TextMessage.class));

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(2)).sendMessage(captor.capture());

        List<TextMessage> capturedMessages = captor.getAllValues();
        assertEquals("user123: test1", capturedMessages.get(0).getPayload());
        assertEquals("user123: test2", capturedMessages.get(1).getPayload());
    }

}
