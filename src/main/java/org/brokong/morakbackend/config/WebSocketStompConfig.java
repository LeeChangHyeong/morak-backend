package org.brokong.morakbackend.config;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.chat.service.WebSocketSessionService;
import org.brokong.morakbackend.global.jwt.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

	private final JwtUtil jwtUtil;
	private final WebSocketSessionService webSocketSessionService;

	// 메시지 브로커 설정
	// 클라이언트가 메시지를 구독할 때와 메시지를 보낼 때의 경로를 설정
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// 메시지 구독 경로 설정 + 하트비트 설정
		config.enableSimpleBroker("/sub")
				.setHeartbeatValue(new long[] {10000, 10000}); // 10초마다 서버↔클라이언트 핑퐁

		// 클라이언트에서 메시지 보낼때 사용하는 경로
		config.setApplicationDestinationPrefixes("/pub");

		log.info("STOMP 메시지 브로커 설정 완료");
		log.info("구독 경로: /sub");
		log.info("메시지 보내는 경로: /pub");
		log.info("하트비트 설정: 10초");
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

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					handleConnect(accessor);
				} else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
					handleDisconnect(accessor);
				}
				return message;
			}
		});
	}

	private void handleConnect(StompHeaderAccessor accessor) {
		String authHeader = accessor.getFirstNativeHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("!!!!!!Authorization 헤더가 없거나 형식이 잘못됨!!!!!!");
			throw new MessagingException("JWT 인증 실패: 토큰 누락");
		}

		String token = authHeader.substring(7);

		try {
			if (jwtUtil.validateAccessToken(token)) {

				String sessionId = accessor.getSessionId();
				Long userId = jwtUtil.getUserIdFromAccessToken(token);
				String nickname = jwtUtil.getNicknameFromAccessToken(token);

				webSocketSessionService.saveSession(sessionId, userId, nickname);

				log.info("WebSocket 인증 성공: {}", jwtUtil.getNicknameFromAccessToken(token));
			}
		} catch (JwtException e) {
			log.warn("WebSocket JWT 검증 실패: {}", e.getMessage());
			throw new MessagingException("JWT 인증 실패: 유효하지 않은 토큰");
		}

	}

	private void handleDisconnect(StompHeaderAccessor accessor) {
		String sessionId = accessor.getSessionId();
		if(sessionId != null) {
			webSocketSessionService.removeSession(sessionId);
			log.info("WebSocket 연결 해제 및 세션 삭제: sessionId={}", sessionId);
		}
	}


}
