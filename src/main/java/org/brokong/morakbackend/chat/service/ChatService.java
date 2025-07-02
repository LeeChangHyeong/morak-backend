package org.brokong.morakbackend.chat.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.dto.ChatMessageDto;
import org.brokong.morakbackend.chat.entity.ChatMessage;
import org.brokong.morakbackend.chat.enums.MessageType;
import org.brokong.morakbackend.chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageDto saveAndGetChatMessage(Long roomId, String message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .type(MessageType.CHAT)
                .chatRoomId(roomId)
                .senderNickname("이창형")
                .message(message)
                .build();

        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageDto.builder()
                .type(savedChatMessage.getType())
                .roomId(savedChatMessage.getChatRoomId())
                .senderNickname(savedChatMessage.getSenderNickname())
                .message(savedChatMessage.getMessage())
                .build();
    }
}
