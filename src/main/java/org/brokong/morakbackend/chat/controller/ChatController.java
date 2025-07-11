package org.brokong.morakbackend.chat.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.dto.ChatMessageDto;
import org.brokong.morakbackend.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/chat")
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
