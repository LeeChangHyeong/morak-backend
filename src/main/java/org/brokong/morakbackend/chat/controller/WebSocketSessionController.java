package org.brokong.morakbackend.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.service.WebSocketSessionService;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("${api.prefix}/websocket")
@RequiredArgsConstructor
@Tag(name = "WebSocket Session", description = "WebSocket 세션 관리 API")
public class WebSocketSessionController {

    private final WebSocketSessionService webSocketSessionService;

    @Operation(summary = "사용자 접속 상태 확인", description = "특정 사용자가 현재 접속 중인지 확인합니다")
    @GetMapping("/users/{userId}/online")
    public ResponseEntity<ResponseDto<Boolean>> checkUserOnline(@PathVariable Long userId) {
        boolean isOnline = webSocketSessionService.isUserOnline(userId);
        return ResponseEntity.ok(
            ResponseDto.success("사용자 접속 상태", isOnline)
        );
    }

    @Operation(summary = "사용자 세션 목록 조회", description = "특정 사용자의 모든 활성 세션을 조회합니다 (다중 탭)")
    @GetMapping("/users/{userId}/sessions")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getUserSessions(@PathVariable Long userId) {
        Set<String> sessions = webSocketSessionService.getUserSessions(userId);
        Long sessionCount = webSocketSessionService.getUserSessionCount(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("sessionCount", sessionCount);
        result.put("sessions", sessions);
        result.put("isOnline", sessionCount > 0);
        
        return ResponseEntity.ok(
            ResponseDto.success("사용자 세션 정보", result)
        );
    }

    @Operation(summary = "세션 개수 조회", description = "특정 사용자의 활성 세션 개수를 조회합니다")
    @GetMapping("/users/{userId}/session-count")
    public ResponseEntity<ResponseDto<Long>> getSessionCount(@PathVariable Long userId) {
        Long count = webSocketSessionService.getUserSessionCount(userId);
        return ResponseEntity.ok(
            ResponseDto.success("세션 개수", count)
        );
    }
}
