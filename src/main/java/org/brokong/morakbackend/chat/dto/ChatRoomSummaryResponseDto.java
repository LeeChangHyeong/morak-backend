package org.brokong.morakbackend.chat.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.enums.ChatRoomType;

@Data
@RequiredArgsConstructor
public class ChatRoomSummaryResponseDto {
	private Long roomId;
	private String roomName;
	private LocalDateTime lastMessageTime;
	private String lastMessage;
	private ChatRoomType roomType;
}
