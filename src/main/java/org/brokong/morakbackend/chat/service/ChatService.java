package org.brokong.morakbackend.chat.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.dto.ChatMessageDto;
import org.brokong.morakbackend.chat.entity.ChatMessage;
import org.brokong.morakbackend.chat.entity.ChatRoom;
import org.brokong.morakbackend.chat.repository.ChatMessageRepository;
import org.brokong.morakbackend.chat.repository.ChatRoomRepository;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final UserRepository userRepository;

	// 메시지 저장
	public void saveMessage(ChatMessageDto messageDto, UserPrincipal userPrincipal) {
		ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getRoomId()).orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
		User sender = userRepository.findById(userPrincipal.getId())
									.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
		ChatMessage chatMessage = ChatMessage.builder()
			.chatRoom(chatRoom)
			.sender(sender)
			.content(messageDto.getMessage())
			.build();

		chatMessageRepository.save(chatMessage);
	}

}
