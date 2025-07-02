package org.brokong.morakbackend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.chat.dto.ChatMessageDto;
import org.brokong.morakbackend.chat.dto.WebSocketSessionDto;
import org.brokong.morakbackend.chat.entity.ChatMessage;
import org.brokong.morakbackend.chat.enums.MessageType;
import org.brokong.morakbackend.chat.repository.ChatMessageRepository;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final WebSocketSessionService webSocketSessionService;

    public ChatMessageDto saveAndGetChatMessage(Long roomId, String message, String sessionId) {

        // 레디스에서 세션 정보 조회
        WebSocketSessionDto userInfo = webSocketSessionService.getSession(sessionId);

        String senderNickname = null;

        if (userInfo != null) {
            senderNickname = userInfo.getNickname();
            log.info("채팅 메시지 전송: roomId={}, sessionId={}, nickname={}",
                     roomId, sessionId, senderNickname);
        } else {
            log.warn("세션 정보를 찾을 수 없습니다: sessionId={}", sessionId);
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .type(MessageType.CHAT)
                .chatRoomId(roomId)
                .senderNickname(senderNickname)
                .message(message)
                .build();

        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageDto.createChatMessage(
                savedChatMessage.getChatRoomId(),
                savedChatMessage.getSenderNickname(),
                savedChatMessage.getMessage()
        );
    }
}
