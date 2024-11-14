package com.dlvb.redischat.websocket;

import org.springframework.web.socket.WebSocketSession;

public class WebSocketHelper {
    public static final String USER_ID_KEY = "userId";

    public static String getUserIdFromSessionAttribute(WebSocketSession webSocketSession) {
        return (String) webSocketSession.getAttributes().get(USER_ID_KEY);
    }

    public static String getUserIdFromUrl(String url){
        return url.substring(url.lastIndexOf('/') + 1);
    }
}