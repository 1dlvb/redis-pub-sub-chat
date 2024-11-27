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
class WebSocketHelperTests {

    @Mock
    private WebSocketSession webSocketSession;

    @Test
    void testGetUserIdFromSessionAttributeReturnsProperUserId() {
        String expectedUserId = "12345";
        when(webSocketSession.getAttributes()).thenReturn(Map.of(WebSocketHelper.USER_ID_KEY, expectedUserId));

        assertEquals(expectedUserId, WebSocketHelper.getUserIdFromSessionAttribute(webSocketSession));
    }

    @Test
    void testGetUserIdFromUrlReturnsUserIdFromUrl() {
        String url = "https://example.com/users/12345";

        assertEquals("12345", WebSocketHelper.getUserIdFromUrl(url));
    }
}
