package org.brokong.morakbackend.chat.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.dto.ChatRoomCreateRequestDto;
import org.brokong.morakbackend.chat.entity.ChatRoom;
import org.brokong.morakbackend.chat.entity.ChatRoomMember;
import org.brokong.morakbackend.chat.enums.ChatRoomType;
import org.brokong.morakbackend.chat.repository.ChatRoomRepository;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createChatRoom(UserPrincipal userPrincipal, ChatRoomCreateRequestDto chatRoomCreateRequestDto) {

		Long userId = userPrincipal.getId();
		Long friendId = chatRoomCreateRequestDto.getFriendId();

		// 두 유저로 이미 존재하는 채팅방 조회
		Optional<ChatRoom> existingRoom = chatRoomRepository
			.findDirectChatRoomByUserIds(userId, friendId, ChatRoomType.DIRECT);

		if (existingRoom.isPresent()) {
			throw new RuntimeException("이미 존재하는 채팅방입니다.");
		}

		// 없으면 새 방 생성 (유저 조회 포함)
		User user = userRepository.findById(userId)
								   .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
		User friend = userRepository.findById(friendId)
								   .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

		ChatRoom newRoom = ChatRoom.builder()
			.type(ChatRoomType.DIRECT)
			.build();

		ChatRoomMember member1 = new ChatRoomMember(newRoom, user, LocalDateTime.now());
		ChatRoomMember member2 = new ChatRoomMember(newRoom, user, LocalDateTime.now());

		newRoom.getMembers().add(member1);
		newRoom.getMembers().add(member2);

		chatRoomRepository.save(newRoom);
	}
}
