package org.brokong.morakbackend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.chat.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

	// 특정 사용자나 방에 메시지를 보내기 위한 템플릿
	private final SimpMessagingTemplate messagingTemplate;

	// 채팅방 입장 처리
	// /ws/chat.join
	@MessageMapping("/chat.join")
	public void joinChatRoom(@Payload ChatMessageDto chatMessageDto, SimpMessageHeaderAccessor headerAccessor) {

		// 웹소켓 세션에 사용자 정보 저장 (연결 추적용)
		headerAccessor.getSessionAttributes().put("nickname", chatMessageDto.getSenderNickname());
		headerAccessor.getSessionAttributes().put("roomId", chatMessageDto.getRoomId());

		log.info("사용자 입장 - 방: {}, 사용자: {}",
				 chatMessageDto.getRoomId(), chatMessageDto.getSenderNickname());

		// 해당 채팅방 구독자들에게 입장 메시지 브로드캐스트
		ChatMessageDto joinMessage = ChatMessageDto.createJoinMessage(chatMessageDto.getRoomId(), chatMessageDto.getSenderNickname());

		// TODO: 여기서 메시지를 DB에 저장하는 로직 추가 예정
		// chatService.saveMessage(joinMessage);

		messagingTemplate.convertAndSend(
			"/topic/chatroom/" + chatMessageDto.getRoomId(), joinMessage
		);
	}

	// 채팅 메시지 전송 처리
	@MessageMapping("/chat.sendMessage")
	public void sendMessage(@Payload ChatMessageDto chatMessageDto) {
		log.info("메시지 전송 - 방: {}, 보낸이: {}, 내용: {}",
				 chatMessageDto.getRoomId(), chatMessageDto.getSenderNickname(), chatMessageDto.getMessage());

		// TODO: 여기서 메시지를 DB에 저장하는 로직 추가 예정
		// chatService.saveMessage(chatMessage);

		messagingTemplate.convertAndSend(
			"/topic/chatroom/" + chatMessageDto.getRoomId(), chatMessageDto
		);
	}

	// 채팅방 나가기 처리
	@MessageMapping("/chat.leave")
	public void leaveChatRoom(@Payload ChatMessageDto chatMessageDto) {

		log.info("사용자 퇴장 - 방: {}, 사용자: {}",
				 chatMessageDto.getRoomId(), chatMessageDto.getSenderNickname());

		ChatMessageDto leaveMessage = ChatMessageDto.createLeaveMessage(chatMessageDto.getRoomId(), chatMessageDto.getSenderNickname());

		// TODO: 여기서 메시지를 DB에 저장하는 로직 추가 예정
		// chatService.saveMessage(leaveMessage);

		messagingTemplate.convertAndSend(
			"/topic/chatroom/" + chatMessageDto.getRoomId(), leaveMessage
		);
	}

}
