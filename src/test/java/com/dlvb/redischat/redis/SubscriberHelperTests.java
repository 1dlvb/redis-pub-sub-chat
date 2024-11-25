package com.dlvb.redischat.redis;

import com.dlvb.redischat.websocket.WebSocketSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriberHelperTests {

    @Mock
    private WebSocketSessionManager webSocketSessionManager;

    @Mock
    private WebSocketSession webSocketSession;

    private SubscriberHelper subscriberHelper;

    @BeforeEach
    void setUp() {
        subscriberHelper = new SubscriberHelper(webSocketSessionManager);
    }

    @Test
    void testMessageHandlesSending() throws IOException {
        String testChannel = "test-channel";
        String testMessage = "test-message";

        when(webSocketSessionManager.getWebSocketSessions(testChannel)).thenReturn(webSocketSession);

        subscriberHelper.message(testChannel, testMessage);

        verify(webSocketSession).sendMessage(any(TextMessage.class));

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(webSocketSession).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();
        assertEquals(testMessage, capturedMessage.getPayload());
    }

}
