package org.brokong.morakbackend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.chat.dto.ChatMessageDto;
import org.brokong.morakbackend.chat.entity.ChatMessage;
import org.brokong.morakbackend.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 일반 채팅
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage chat(@DestinationVariable Long roomId, @Payload ChatMessageDto chatMessageDto) {
        return chatService.saveAndGetChatMessage(roomId, chatMessageDto);
    }


}
