package org.brokong.morakbackend.chat.handler;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.global.jwt.JwtUtil;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.UserStatus;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 연결된 사용자들을 저장하는 맵
    // Key: 사용자 ID, Value: WebSocket 세션
    private final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    // 세션 ID와 사용자 정보를 매핑하는 맵
    private final ConcurrentHashMap<String, User> sessionUserMap = new ConcurrentHashMap<>();

    // 사용자가 WebSocket에 연결했을 때 호출되는 메서드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // 1. JWT 토큰에서 사용자 정보 추출
            User user = getUserFromSession(session);
            
            if (user == null) {
                log.warn("유효하지 않은 토큰으로 WebSocket 연결 시도");
                session.close();
                return;
            }

            // 2. 사용자 상태 검증
            if (user.getStatus() == UserStatus.BLOCKED) {
                log.warn("차단된 사용자의 WebSocket 연결 시도: {}", user.getEmail());
                session.close();
                return;
            }

            if (user.getStatus() == UserStatus.WITHDRAWN) {
                log.warn("탈퇴한 사용자의 WebSocket 연결 시도: {}", user.getEmail());
                session.close();
                return;
            }

            // 3. 세션 저장
            String sessionId = session.getId();
            sessions.put(user.getId(), session);
            sessionUserMap.put(sessionId, user);

            log.info("===== WebSocket 연결 성공! ====");
            log.info("User connected: {} ({})", user.getNickname(), user.getEmail());
            log.info("현재 연결된 사용자 수: {}명", sessions.size());

            // 4. 연결된 사용자에게 알림
            String welcomeMessage = String.format("%s님, 채팅방에 입장하셨습니다.", user.getNickname());
            session.sendMessage(new TextMessage(welcomeMessage));

            // 5. 다른 사용자들에게 입장 알림
            broadcastToOthers(user.getId(), user.getNickname() + "님이 채팅방에 입장했습니다.");

        } catch (Exception e) {
            log.error("WebSocket 연결 처리 중 오류 발생: {}", e.getMessage(), e);
            session.close();
        }
    }

    // 클라이언트로부터 메시지를 받았을 때 호출되는 메서드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        User user = sessionUserMap.get(sessionId);
        
        if (user == null) {
            log.warn("인증되지 않은 세션에서 메시지 수신: {}", sessionId);
            session.close();
            return;
        }

        String receivedMessage = message.getPayload();
        log.info("Message received from {} ({}): {}", user.getNickname(), user.getEmail(), receivedMessage);

        // 메시지 브로드캐스트
        String formattedMessage = String.format("%s: %s", user.getNickname(), receivedMessage);
        broadcastToAll(formattedMessage);
    }

    // 연결이 끊어졌을 때 호출되는 메서드
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        User user = sessionUserMap.get(sessionId);
        
        if (user != null) {
            sessions.remove(user.getId());
            sessionUserMap.remove(sessionId);

            log.info("WebSocket 연결 종료 - 사용자: {} ({})", user.getNickname(), user.getEmail());
            log.info("현재 연결된 사용자 수: {}명", sessions.size());

            // 다른 사용자들에게 퇴장 알림
            broadcastToOthers(user.getId(), user.getNickname() + "님이 채팅방을 나갔습니다.");
        } else {
            log.info("WebSocket 연결 종료 - 세션 ID: {}", sessionId);
        }
    }

    // 에러 발생시 호출되는 메서드
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();
        User user = sessionUserMap.get(sessionId);
        
        if (user != null) {
            log.error("WebSocket 연결 중 에러 발생 - 사용자: {} ({}), 에러: {}", 
                     user.getNickname(), user.getEmail(), exception.getMessage());
            sessions.remove(user.getId());
            sessionUserMap.remove(sessionId);
        } else {
            log.error("WebSocket 연결 중 에러 발생 - 세션 ID: {}, 에러: {}", sessionId, exception.getMessage());
        }

        if (session.isOpen()) {
            session.close();
        }
    }

    // 모든 연결된 사용자에게 메시지를 보내는 메서드
    private void broadcastToAll(String message) {
        log.info("모든 연결된 사용자에게 전송: {}", message);

        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                log.error("메시지 전송 중 오류 발생: {}", e.getMessage());
            }
        });
    }

    // 특정 사용자를 제외한 모든 사용자에게 메시지 전송
    private void broadcastToOthers(Long excludeUserId, String message) {
        log.info("사용자 {}를 제외한 모든 사용자에게 전송: {}", excludeUserId, message);

        sessions.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(excludeUserId))
                .forEach(entry -> {
                    try {
                        WebSocketSession session = entry.getValue();
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(message));
                        }
                    } catch (IOException e) {
                        log.error("메시지 전송 중 오류 발생: {}", e.getMessage());
                    }
                });
    }

    // JWT 토큰에서 사용자 정보 추출
    private User getUserFromSession(WebSocketSession session) {
        try {
            // WebSocket 연결 시 URL에서 쿼리 파라미터 추출
            URI uri = session.getUri();
            log.info("WebSocket URI: {}", uri);
            
            if (uri == null) {
                log.warn("WebSocket URI가 null입니다.");
                return null;
            }

            String query = uri.getQuery();
            log.info("Query string: {}", query);
            
            if (query == null || query.isEmpty()) {
                log.warn("쿼리 파라미터가 없습니다.");
                return null;
            }

            // 쿼리 파라미터에서 토큰 추출 (예: ws://localhost:8080/chat?token=eyJhbGciOi...)
            String token = null;
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    token = param.substring(6); // "token="을 제거
                    break;
                }
            }

            log.info("추출된 토큰: {}", token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null");

            if (token == null || token.isEmpty()) {
                log.warn("토큰이 없습니다.");
                return null;
            }

            // JWT 토큰 검증
            log.info("토큰 검증 시작...");
            boolean isValid = jwtUtil.validateAccessToken(token);
            log.info("토큰 검증 결과: {}", isValid);
            
            if (!isValid) {
                log.warn("유효하지 않은 토큰입니다.");
                return null;
            }

            // 토큰에서 이메일 추출
            String email = jwtUtil.getEmailFromAccessToken(token);
            log.info("토큰에서 추출된 이메일: {}", email);
            
            // 사용자 정보 조회
            User user = userRepository.findByEmail(email).orElse(null);
            log.info("조회된 사용자: {}", user != null ? user.getNickname() : "null");
            
            return user;

        } catch (Exception e) {
            log.error("사용자 정보 추출 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
}
