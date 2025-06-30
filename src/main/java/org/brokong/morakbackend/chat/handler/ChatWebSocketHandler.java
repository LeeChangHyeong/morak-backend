package org.brokong.morakbackend.chat.handler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

	// 연결된 사용자들을 저장하는 맵
	// Key: 세션 ID, Value: WebSocket 세션
	private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	// 사용자가 WebSocket에 연결했을 때 호출되는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String sessionId = session.getId();
		sessions.put(sessionId, session);

		log.info("===== WebSocket 연결 성공! ====");
		log.info("User connected: {}", sessionId);
		log.info("현재 연결된 사용자 수: {}명", sessions.size());

		// 연결된 사용자에게 알림
		String welcomeMessage = "채팅 연결에 성공했습니다.";
		session.sendMessage(new TextMessage(welcomeMessage));
	}

	// 클라이언트로부터 메시지를 받았을 때 호출되는 메서드
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String sessionId = session.getId();
		String receivedMessage = message.getPayload();

		log.info("Message received from {}: {}", sessionId, receivedMessage);

		broadcastToAll(sessionId + ": " + receivedMessage);
	}

	// 연결이 끊어졌을 때 호출되는 메서드
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String sessionId = session.getId();
		sessions.remove(sessionId);

		log.info("WebSocket 연결 종료 - 세션 ID: {}", sessionId);
		log.info("현재 연결된 사용자 수: {}명", sessions.size());

		broadcastToAll(sessionId + "님이 채팅방을 나갔습니다.");
	}

	// 에러 발생시 호출되는 메서드
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		String sessionId = session.getId();
		log.error("WebSocket 연결 중 에러 발생 - 세션 ID: {}, 에러: {}", sessionId, exception.getMessage());

		// 에러 발생시 해당 세션 정리
		sessions.remove(sessionId);

		if (session.isOpen()) {
			session.close();
		}
	}


	// 모든 연결된 사용자에게 메시지를 보내는 메서드
	private void broadcastToAll(String message) {
		log.info("모든 연결된 사용자에게 전송: {}", message);

		sessions.values().forEach(sessions -> {
			try {
				if (sessions.isOpen()) {
					sessions.sendMessage(new TextMessage(message));
				}
			} catch (IOException e) {
				log.error("메시지 전송 중 오류 발생: {}", e.getMessage());
			}
		});
	}

}
