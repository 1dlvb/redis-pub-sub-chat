package com.dlvb.redischat.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocketSessionManager is responsible for managing WebSocket sessions.
 * @author Matushkin Anton
 */
@Slf4j
@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> webSocketSessionByUserId = new HashMap<>();

    /**
     * Adds a WebSocket session to the manager, associating it with a user ID.
     * The user ID is extracted from the WebSocket session attributes.
     *
     * @param webSocketSession The WebSocket session to be added.
     */
    public void addWebSocketSession(WebSocketSession webSocketSession) {
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(webSocketSession);
        log.info("Got request to add session id {} for user id {} ", webSocketSession.getId(), userId);
        webSocketSessionByUserId.put(userId, webSocketSession);
        log.info("Added session id {} for user id {}", webSocketSession.getId(), userId);
    }

    /**
     * Removes a WebSocket session from the manager, based on the user ID extracted from the session.
     *
     * @param webSocketSession The WebSocket session to be removed.
     */
    public void removeWebSocketSession(WebSocketSession webSocketSession) {
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(webSocketSession);
        log.info("Got request to remove session id {} for user id {}", webSocketSession.getId(), userId);
        webSocketSessionByUserId.remove(userId);
        log.info("Removed session id {} for user id {}", webSocketSession.getId(), userId);
    }

    /**
     * Retrieves the WebSocket session associated with the given user ID.
     *
     * @param userId The user ID whose WebSocket session is to be retrieved.
     * @return The WebSocket session associated with the user ID, or {@code null} if not found.
     */
    public WebSocketSession getWebSocketSessions(String userId) {
        return webSocketSessionByUserId.get(userId);
    }

}
