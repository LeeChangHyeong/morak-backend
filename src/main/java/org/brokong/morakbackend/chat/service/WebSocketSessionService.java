package org.brokong.morakbackend.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.chat.dto.WebSocketSessionDto;
import org.brokong.morakbackend.global.redis.RedisKey;
import org.brokong.morakbackend.global.redis.RedisService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketSessionService {

	private final RedisService redisService;
	private final ObjectMapper objectMapper;
	private static final Duration SESSION_TIMEOUT = Duration.ofHours(24);

	public void saveSession(String sessionId, Long userId, String nickname) {
		// 1. 세션 정보 저장
		String sessionKey = RedisKey.sessionKey(sessionId);
		WebSocketSessionDto session = new WebSocketSessionDto(
			userId.toString(),
			nickname,
			LocalDateTime.now()
		);

		try {
			String sessionJson = objectMapper.writeValueAsString(session);
			redisService.setValue(sessionKey, sessionJson, SESSION_TIMEOUT);
			
			// 2. 사용자별 세션 목록에 추가 (다중 탭 관리)
			String userSessionsKey = RedisKey.userSessionsKey(userId);
			redisService.addToSet(userSessionsKey, sessionId);
			
			log.info("WebSocket 세션 저장: sessionId={}, userId={}, nickname={}, 총 세션 수={}",
					 sessionId, userId, nickname, getUserSessionCount(userId));
		} catch (Exception e) {
			log.error("WebSocket 세션 저장 실패: {}", e.getMessage());
		}
	}

	public WebSocketSessionDto getSession(String sessionId) {
		String key = RedisKey.sessionKey(sessionId);

		try {
			String sessionJson = redisService.getValue(key);
			if (sessionJson != null) {
				return objectMapper.readValue(sessionJson, WebSocketSessionDto.class);
			}
		} catch (Exception e) {
			log.error("WebSocket 세션 조회 실패: {}", e.getMessage());
		}
		return null;
	}

	public void removeSession(String sessionId) {
		// 1. 세션 정보 조회
		WebSocketSessionDto session = getSession(sessionId);
		
		if (session != null) {
			// 2. 사용자별 세션 목록에서 제거
			Long userId = Long.parseLong(session.getUserId());
			String userSessionsKey = RedisKey.userSessionsKey(userId);
			redisService.removeFromSet(userSessionsKey, sessionId);
			
			log.info("WebSocket 세션 삭제: sessionId={}, userId={}, 남은 세션 수={}",
					 sessionId, userId, getUserSessionCount(userId));
		}
		
		// 3. 세션 정보 삭제
		String sessionKey = RedisKey.sessionKey(sessionId);
		redisService.deleteValue(sessionKey);
	}

	public boolean isSessionExists(String sessionId) {
		String key = RedisKey.sessionKey(sessionId);
		return redisService.isExists(key);
	}

	// 특정 사용자의 모든 세션 조회 (여러 탭 관리)
	public Set<String> getUserSessions(Long userId) {
		String key = RedisKey.userSessionsKey(userId);
		return redisService.getSetMembers(key);
	}

	// 특정 사용자의 세션 개수 조회
	public Long getUserSessionCount(Long userId) {
		String key = RedisKey.userSessionsKey(userId);
		return redisService.getSetSize(key);
	}

	// 특정 사용자가 현재 접속 중인지 확인
	public boolean isUserOnline(Long userId) {
		Long sessionCount = getUserSessionCount(userId);
		return sessionCount != null && sessionCount > 0;
	}
}
