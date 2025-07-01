package org.brokong.morakbackend.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.brokong.morakbackend.chat.enums.MessageType;

@Data
@Builder
public class ChatMessageDto {

	private MessageType type;
	private Long roomId;
	private String senderNickname;
	private String message;
	private String timestamp;

	// 일반 채팅 메시지 생성
	public static ChatMessageDto createChatMessage(Long roomId, String senderNickname, String message) {
		return ChatMessageDto.builder()
							 .type(MessageType.CHAT)
							 .roomId(roomId)
							 .senderNickname(senderNickname)
							 .message(message)
							 .timestamp(LocalDateTime.now().toString())
							 .build();
	}

	// 입장 메시지 생성
	public static ChatMessageDto createJoinMessage(Long roomId, String senderNickname) {
		return ChatMessageDto.builder()
							 .type(MessageType.JOIN)
							 .roomId(roomId)
							 .senderNickname(senderNickname)
							 .message(senderNickname + "님이 입장하셨습니다.")
							 .timestamp(LocalDateTime.now().toString())
							 .build();
	}

	// 퇴장 메시지 생성
	public static ChatMessageDto createLeaveMessage(Long roomId, String senderNickname) {
		return ChatMessageDto.builder()
							 .type(MessageType.LEAVE)
							 .roomId(roomId)
							 .senderNickname(senderNickname)
							 .message(senderNickname + "님이 퇴장하셨습니다.")
							 .timestamp(LocalDateTime.now().toString())
							 .build();
	}

}
