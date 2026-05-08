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

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Messages from server
        config.enableSimpleBroker("/topic");
        // Messages from client
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(
                            ServerHttpRequest request,
                            ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Map<String, Object> attributes) {

                        System.out.println("==== Handshake start ====");
                        System.out.println("URI: " + request.getURI());

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
                                        System.out.println("HANDSHAKE OK for user: " + userId);
                                        return true;
                                    } catch (Exception e) {
                                        System.out.println("HANDSHAKE FAILED: " + e.getMessage());
                                        return false;
                                    }
                                }
                            }
                        }
                        System.out.println("NO TOKEN IN HANDSHAKE - rejecting");
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
        System.out.println("==== configureClientInboundChannel registered at startup ====");

        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                System.out.println("==== preSend called ====");
                System.out.println("Command: " + (accessor != null ? accessor.getCommand() : "null accessor"));

                if (accessor == null) return message;

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Map<String, Object> attributes = accessor.getSessionAttributes();
                    System.out.println("Session attributes: " + attributes);

                    if (attributes != null && attributes.get("userId") != null) {
                        Long userId = (Long) attributes.get("userId");
                        accessor.setUser(() -> userId.toString());
                        System.out.println("USER SET FROM HANDSHAKE: " + userId);
                    } else {
                        System.out.println("NO USER ID IN SESSION ATTRIBUTES - rejecting");
                        return null;  // ← afvis CONNECT eksplicit
                    }
                }

                return message;
            }
        });
    }
}
