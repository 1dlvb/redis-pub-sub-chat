package com.dlvb.redischat.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WebSocketSessionManager {
    private final Map<String, WebSocketSession> webSocketSessionByUserId = new HashMap<>();

    public void addWebSocketSession(WebSocketSession webSocketSession){
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(webSocketSession);
        log.info("Got request to add session id {} for user id {} ", webSocketSession.getId(), userId);
        webSocketSessionByUserId.put(userId,webSocketSession);
        log.info("Added session id {} for user id {}", webSocketSession.getId(), userId);
    }

    public void removeWebSocketSession(WebSocketSession webSocketSession){
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(webSocketSession);
        log.info("Got request to remove session id {} for user id {}", webSocketSession.getId(), userId);
        webSocketSessionByUserId.remove(userId);
        log.info("Removed session id {} for user id {}", webSocketSession.getId(), userId);
    }

    public WebSocketSession getWebSocketSessions(String userId){
        return webSocketSessionByUserId.get(userId);
    }
}