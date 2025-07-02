package org.brokong.morakbackend.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.chat.dto.WebSocketSessionDto;
import org.brokong.morakbackend.global.redis.RedisService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketSessionService {

	private final RedisService redisService;
	private final ObjectMapper objectMapper;
	private static final String SESSION_PREFIX = "sessionId:";
	private static final Duration SESSION_TIMEOUT = Duration.ofHours(24);

	// 레디스에 sessionId : 유저 정보 저장
	public void saveSession(String sessionId, Long userId, String nickname) {
		String key = SESSION_PREFIX + sessionId;
		WebSocketSessionDto session = new WebSocketSessionDto(
			userId.toString(),
			nickname,
			LocalDateTime.now()
		);

		try {
			String sessionJson = objectMapper.writeValueAsString(session);
			redisService.setValue(key, sessionJson, SESSION_TIMEOUT);
			log.info("WebSocket 세션 저장: key={}, userId={}, nickname={}",
					 key, userId, nickname);
		} catch (Exception e) {
			log.error("WebSocket 세션 저장 실패: {}", e.getMessage());
		}
	}

	public WebSocketSessionDto getSession(String sessionId) {
		String key = SESSION_PREFIX + sessionId;

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
		String key = SESSION_PREFIX + sessionId;
		redisService.deleteValue(key);
		log.info("WebSocket 세션 삭제: key={}", key);
	}

	public boolean isSessionExists(String sessionId) {
		String key = SESSION_PREFIX + sessionId;
		return redisService.isExists(key);
	}
}