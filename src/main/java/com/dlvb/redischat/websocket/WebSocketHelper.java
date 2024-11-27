package com.dlvb.redischat.websocket;

import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocketHelper is a utility class that provides helper methods to extract information
 * from WebSocket sessions and URLs.
 * @author Matushkin Anton
 */
public final class WebSocketHelper {

    public static final String USER_ID_KEY = "userId";

    private WebSocketHelper() {}

    /**
     * Retrieves the user ID from the WebSocket session attributes.
     * The user ID is expected to be stored in the session attributes under the {@link #USER_ID_KEY}.
     *
     * @param webSocketSession The WebSocket session from which the user ID will be extracted.
     * @return The user ID stored in the WebSocket session attributes, or {@code null} if not found.
     */
    public static String getUserIdFromSessionAttribute(WebSocketSession webSocketSession) {
        return (String) webSocketSession.getAttributes().get(USER_ID_KEY);
    }

    /**
     * Extracts the user ID from the URL string. The user ID is assumed to be the last segment
     * of the URL.
     *
     * @param url The URL string from which the user ID will be extracted.
     * @return The user ID extracted from the URL.
     */
    public static String getUserIdFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

}
