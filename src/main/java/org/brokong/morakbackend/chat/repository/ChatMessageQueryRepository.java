package org.brokong.morakbackend.chat.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.brokong.morakbackend.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageQueryRepository {

	// 채팅방의 메시지 히스토리 조회 (페이징)
	Page<ChatMessage> findMessagesByChatRoomId(Long chatRoomId, Pageable pageable);

	// 채팅방의 최근 메시지들 조회
	List<ChatMessage> findRecentMessagesByChatRoomId(Long chatRoomId, int limit);

	// 읽지 않은 메시지 수 조회
	Long countUnreadMessages(Long chatRoomId, Long userId, LocalDateTime lastReadAt);

	// 채팅방의 마지막 메시지 조회
	Optional<ChatMessage> findLastMessageByChatRoomId(Long chatRoomId);

}
