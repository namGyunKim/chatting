package kr.namgyun.chatting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ChatConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * Client에서 websocket연결할 때 사용할 API 경로를 설정해주는 메서드
         */
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*");
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        /**
         * queue는 1대1
         * topic은 1대 다에서 사용
         */
        registry.enableSimpleBroker("/queue", "/topic");

        /**
         * 메시지 보낼때 경로 관련 설정
         */
        registry.setApplicationDestinationPrefixes("/app");
    }
}
