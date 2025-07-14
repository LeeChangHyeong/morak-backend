package org.brokong.morakbackend.chat.dto;

import lombok.Data;
import org.brokong.morakbackend.chat.enums.ChatRoomType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatRoomDetailResponseDto {
    private Long roomId;
    private String roomName;
    private ChatRoomType roomType;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private String lastMessage;
    private List<ChatRoomMemberDto> members;
    private List<ChatMessageDto> recentMessages; // 최근 메시지들
    private Long totalMessageCount;
    private Long unreadMessageCount;
}