package org.brokong.morakbackend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.chat.dto.ChatMessageDto;
import org.brokong.morakbackend.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 일반 채팅
    @MessageMapping("/chatRoom/{roomId}")
    @SendTo("/sub/chatRoom/{roomId}")
    public ChatMessageDto chat(@DestinationVariable Long roomId,
                               @Payload String message,
                               StompHeaderAccessor headerAccessor) {

        String sessionId = headerAccessor.getSessionId();

        return chatService.saveAndGetChatMessage(roomId, message, sessionId);
    }


}
