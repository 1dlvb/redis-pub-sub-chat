package com.dlvb.redischat.websocket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketSessionManagerTests {

    private final WebSocketSessionManager sessionManager = new WebSocketSessionManager();

    @Mock
    private WebSocketSession mockSession;

    @Test
    void testAddWebSocketSessionAddsWebsocket() {
        String userId = "12345";
        when(mockSession.getId()).thenReturn("sessionId");
        when(mockSession.getAttributes()).thenReturn(Map.of(WebSocketHelper.USER_ID_KEY, userId));

        sessionManager.addWebSocketSession(mockSession);

        assertEquals(mockSession, sessionManager.getWebSocketSessions(userId));
    }

    @Test
    void testRemoveWebSocketSessionRemovesWebsocket() {
        String userId = "12345";
        when(mockSession.getId()).thenReturn("sessionId");
        when(mockSession.getAttributes()).thenReturn(Map.of(WebSocketHelper.USER_ID_KEY, userId));
        sessionManager.addWebSocketSession(mockSession);

        sessionManager.removeWebSocketSession(mockSession);

        assertEquals(null, sessionManager.getWebSocketSessions(userId));
    }

    @Test
    void testGetWebSocketSessionReturnsSession() {
        String userId = "12345";
        when(mockSession.getId()).thenReturn("sessionId");
        when(mockSession.getAttributes()).thenReturn(Map.of(WebSocketHelper.USER_ID_KEY, userId));
        sessionManager.addWebSocketSession(mockSession);

        assertEquals(mockSession, sessionManager.getWebSocketSessions(userId));
    }
}
