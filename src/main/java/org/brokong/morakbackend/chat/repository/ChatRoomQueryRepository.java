package org.brokong.morakbackend.chat.repository;

import java.util.List;
import java.util.Optional;
import org.brokong.morakbackend.chat.entity.ChatRoom;

public interface ChatRoomQueryRepository {

	// 두 사용자 간의 1대1 채팅방 찾기
	Optional<ChatRoom> findDirectChatRoomBetweenUsers(Long user1Id, Long user2Id);

	// 사용자가 참여한 활성 채팅방 조회 (최근순)
	List<ChatRoom> findActiveRoomsByUserId(Long userId);

	// 채팅방별 읽지 않은 메시지 수와 함께 조회
	List<ChatRoom> findRoomsWithUnreadCountByUserId(Long userId);

}
