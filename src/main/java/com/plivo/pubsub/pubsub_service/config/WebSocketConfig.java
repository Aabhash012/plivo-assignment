package com.plivo.pubsub.pubsub_service.config;

import com.plivo.pubsub.pubsub_service.websocket.PubSubWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PubSubWebSocketHandler handler;

    public WebSocketConfig(PubSubWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws").setAllowedOrigins("*");
    }
}