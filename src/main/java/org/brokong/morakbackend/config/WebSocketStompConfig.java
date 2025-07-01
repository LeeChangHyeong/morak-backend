package org.brokong.morakbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

	// 메시지 브로커 설정
	// 클라이언트가 메시지를 구독할 때와 메시지를 보낼 때의 경로를 설정
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// 메시지 구독 경로 설정
		// 예: /topic/public, /topic/chatroom/123
		config.enableSimpleBroker("/topic", "/queue");

		// 클라이언트에서 메시지 보낼때 사용하는 경로
		config.setApplicationDestinationPrefixes("/app");

		log.info("STOMP 메시지 브로커 설정 완료");
		log.info("구독 경로: /topic (단체), /queue (개인)");
		log.info("메시지 보내는 경로: /app");
	}

	// 클라이언트가 웹소켓에 연결할 때 사용할 엔드포인트
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws") // 엔드포인트 경로
			.setAllowedOriginPatterns("*") // CORS 설정
			.setAllowedOrigins("http://localhost:8080") // 명시적 허용
			.withSockJS(); // SockJS 풀백 옵션

		log.info("STOMP 엔드포인트 설정: /ws");
		log.info("SockJS 사용 설정");
	}

}
