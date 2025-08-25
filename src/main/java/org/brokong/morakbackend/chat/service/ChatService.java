package org.brokong.morakbackend.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.chat.dto.ChatMessageDto;
import org.brokong.morakbackend.chat.dto.WebSocketSessionDto;
import org.brokong.morakbackend.chat.entity.ChatMessage;
import org.brokong.morakbackend.chat.entity.ChatRoom;
import org.brokong.morakbackend.chat.enums.MessageType;
import org.brokong.morakbackend.chat.repository.ChatMessageRepository;
import org.brokong.morakbackend.chat.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final WebSocketSessionService webSocketSessionService;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
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

        // 채팅방 존재 여부 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId));

        ChatMessage chatMessage = ChatMessage.builder()
                .type(MessageType.CHAT)
                .chatRoomId(roomId)
                .senderNickname(senderNickname)
                .message(message)
                .build();

        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        // 채팅방의 마지막 메시지 정보 업데이트
        chatRoom.updateLastMessageTime(message);
        chatRoomRepository.save(chatRoom);

        return ChatMessageDto.createChatMessage(
                savedChatMessage.getChatRoomId(),
                savedChatMessage.getSenderNickname(),
                savedChatMessage.getMessage()
        );
    }
}
