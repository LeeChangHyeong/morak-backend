package org.brokong.morakbackend.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.brokong.morakbackend.chat.enums.MessageType;

@Data
@Builder
public class ChatMessageDto {

	private MessageType type;
	private String roomId;
	private String sender;
	private String message;
	private String timestamp;

	// 일반 채팅 메시지 생성
	public static ChatMessageDto createChatMessage(String roomId, String sender, String message) {
		return ChatMessageDto.builder()
							 .type(MessageType.CHAT)
							 .roomId(roomId)
							 .sender(sender)
							 .message(message)
							 .timestamp(LocalDateTime.now().toString())
							 .build();
	}

	// 입장 메시지 생성
	public static ChatMessageDto createJoinMessage(String roomId, String sender) {
		return ChatMessageDto.builder()
							 .type(MessageType.JOIN)
							 .roomId(roomId)
							 .sender(sender)
							 .message(sender + "님이 입장하셨습니다.")
							 .timestamp(LocalDateTime.now().toString())
							 .build();
	}

	// 퇴장 메시지 생성
	public static ChatMessageDto createLeaveMessage(String roomId, String sender) {
		return ChatMessageDto.builder()
							 .type(MessageType.LEAVE)
							 .roomId(roomId)
							 .sender(sender)
							 .message(sender + "님이 퇴장하셨습니다.")
							 .timestamp(LocalDateTime.now().toString())
							 .build();
	}

}
