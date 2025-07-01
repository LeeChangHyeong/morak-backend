package org.brokong.morakbackend.chat.repository;

import static org.brokong.morakbackend.chat.entity.QChatMessage.chatMessage;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageQueryRepositoryImpl implements ChatMessageQueryRepository {

	private final JPAQueryFactory queryFactory;

	// 채팅방의 메시지 히스토리 조회 (페이징, 최신순)
	@Override
	public Page<ChatMessage> findMessagesByChatRoomId(Long chatRoomId, Pageable pageable) {

		// 메시지 조회 (최신)
		List<ChatMessage> messages = queryFactory
			.selectFrom(chatMessage)
			.leftJoin(chatMessage.sender).fetchJoin() // 보낸사람 정보 함께 조회 (N+1 방지)
			.where(chatMessage.chatRoom.id.eq(chatRoomId))
			.orderBy(chatMessage.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 총 메시지 개수 조회
		Long totalCount = queryFactory
			.select(chatMessage.count())
			.from(chatMessage)
			.where(chatMessage.chatRoom.id.eq(chatRoomId))
			.fetchOne();

		return new PageImpl<>(messages, pageable, totalCount != null ? totalCount : 0L);
	}

	// 채팅방의 최근 메시지들 조회 (개수 제한)
	@Override
	public List<ChatMessage> findRecentMessagesByChatRoomId(Long chatRoomId, int limit) {
		return queryFactory
			.selectFrom(chatMessage)
			.leftJoin(chatMessage.sender).fetchJoin() // N+1 방지
			.where(chatMessage.chatRoom.id.eq(chatRoomId))
			.orderBy(chatMessage.createdAt.desc())
			.limit(limit)
			.fetch();
	}

	// 읽지 않은 메시지 수 조회
	@Override
	public Long countUnreadMessages(Long chatRoomId, Long userId, LocalDateTime lastReadAt) {
		Long count = queryFactory
			.select(chatMessage.count())
			.from(chatMessage)
			.where(
				chatMessage.chatRoom.id.eq(chatRoomId),
				chatMessage.sender.id.ne(userId), // 내가 보낸 메시지 제외
				chatMessage.createdAt.gt(lastReadAt) // 마지막 읽은 시간 이후
			)
			.fetchOne();

		return count != null ? count : 0L;
	}

	// 채팅방의 마지막 메시지 조회
	@Override
	public Optional<ChatMessage> findLastMessageByChatRoomId(Long chatRoomId) {
		ChatMessage lastMessage = queryFactory
			.selectFrom(chatMessage)
			.leftJoin(chatMessage.sender).fetchJoin() // 보낸 사람 정보 함께 조회
			.where(chatMessage.chatRoom.id.eq(chatRoomId))
			.orderBy(chatMessage.createdAt.desc())
			.limit(1)
			.fetchOne();

		return Optional.ofNullable(lastMessage);
	}
}
