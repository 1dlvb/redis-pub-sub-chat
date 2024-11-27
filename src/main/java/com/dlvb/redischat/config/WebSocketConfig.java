package com.dlvb.redischat.config;

import com.dlvb.redischat.redis.Publisher;
import com.dlvb.redischat.redis.Subscriber;
import com.dlvb.redischat.websocket.WebSocketTextHandler;
import com.dlvb.redischat.websocket.WebSocketHelper;
import com.dlvb.redischat.websocket.WebSocketSessionManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Configuration class for setting up WebSocket support in the application.
 *
 * @see WebSocketTextHandler
 * @see WebSocketHelper
 * @see WebSocketSessionManager
 * @see Publisher
 * @see Subscriber
 * @author Matushkin Anton
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    @NonNull
    private WebSocketSessionManager webSocketSessionManager;

    @NonNull
    private Publisher redisPublisher;

    @NonNull
    private Subscriber redisSubscriber;

    @NonNull
    private WebSocketTextHandler webSocketTextHandler;

    /**
     * Registers the WebSocket handler and interceptors.
     * <p>
     * This method adds the {@link WebSocketTextHandler} to handle WebSocket messages on the "/chat/*" endpoint.
     * </p>
     *
     * @param registry the WebSocket handler registry
     */
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketTextHandler, "/chat/*").
                addInterceptors(getParametersInterceptors()).
                setAllowedOriginPatterns("*");
    }

    @Bean
    public HandshakeInterceptor getParametersInterceptors() {
        return new HandshakeInterceptor() {

            /**
             * Intercepts the WebSocket handshake request to extract the user ID from the URL.
             *
             * @param request the HTTP request
             * @param response the HTTP response
             * @param wsHandler the WebSocket handler
             * @param attributes the WebSocket session attributes
             * @return true to proceed with the handshake, false to reject
             */
            public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                           @NonNull ServerHttpResponse response,
                                           @NonNull WebSocketHandler wsHandler,
                                           @NonNull Map<String, Object> attributes) {

                String path = request.getURI().getPath();
                String userId = WebSocketHelper.getUserIdFromUrl(path);
                attributes.put(WebSocketHelper.USER_ID_KEY, userId);
                return true;
            }

            /**
             * After the handshake, no additional actions are required.
             *
             * @param request the HTTP request
             * @param response the HTTP response
             * @param wsHandler the WebSocket handler
             * @param exception any exception thrown during the handshake
             */
            public void afterHandshake(@NonNull ServerHttpRequest request,
                                       @NonNull ServerHttpResponse response,
                                       @NonNull WebSocketHandler wsHandler,
                                       Exception exception) {
            }
        };
    }

}
