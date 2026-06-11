package com.recyclingprojectbackend.config;

import com.recyclingprojectbackend.auth.utility.JwtUtility;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtility jwtUtility;

    public WebSocketConfig(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    /**
     * Configures STOMP message destinations.
     * "/topic" is used for server-to-client messages,
     * while "/app" is used for client-to-server messages.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the WebSocket endpoint and validates
     * the JWT token during the handshake.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new HandshakeInterceptor() {

                    /**
                     * Validates the JWT token before the WebSocket
                     * connection is established.
                     */
                    @Override
                    public boolean beforeHandshake(
                            ServerHttpRequest request,
                            ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Map<String, Object> attributes) {

                        String query = request.getURI().getQuery();
                        if (query != null) {
                            String[] params = query.split("&");
                            for (String param : params) {
                                if (param.startsWith("token=")) {
                                    String token = URLDecoder.decode(
                                            param.substring(6),
                                            StandardCharsets.UTF_8
                                    );
                                    try {
                                        Long userId = jwtUtility.extractUserId(token);
                                        attributes.put("userId", userId);
                                        return true;
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }
                            }
                        }
                        return false;
                    }

                    @Override
                    public void afterHandshake(
                            ServerHttpRequest request,
                            ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Exception exception) {
                    }
                })
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            /**
             * Associates the authenticated user with the
             * WebSocket session during STOMP CONNECT.
             */
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null) return message;

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Map<String, Object> attributes = accessor.getSessionAttributes();
                    System.out.println("Session attributes: " + attributes);

                    if (attributes != null && attributes.get("userId") != null) {
                        Long userId = (Long) attributes.get("userId");
                        accessor.setUser(() -> userId.toString());
                    } else {
                        return null;
                    }
                }
                return message;
            }
        });
    }
}
