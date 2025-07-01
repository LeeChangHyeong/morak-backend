package org.brokong.morakbackend.chat.repository;

import static org.brokong.morakbackend.chat.entity.QChatRoom.chatRoom;
import static org.brokong.morakbackend.chat.entity.QChatRoomMember.chatRoomMember;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.entity.ChatRoom;
import org.brokong.morakbackend.chat.enums.ChatRoomType;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

	private final JPAQueryFactory queryFactory;

	// 1대1 방 찾기
	@Override
	public Optional<ChatRoom> findDirectChatRoomBetweenUsers(Long user1Id, Long user2Id) {

		ChatRoom result = queryFactory
			.selectFrom(chatRoom)
			.where(
				chatRoom.type.eq(ChatRoomType.DIRECT),
				// 첫 번째 사용자가 멤버인 방
				chatRoom.id.in(
					queryFactory
						.select(chatRoomMember.chatRoom.id)
						.from(chatRoomMember)
						.where(chatRoomMember.user.id.eq(user1Id))
				),
				// 두 번째 사용자도 멤버인 방
				chatRoom.id.in(
					queryFactory
						.select(chatRoomMember.chatRoom.id)
						.from(chatRoomMember)
						.where(chatRoomMember.user.id.eq(user2Id))
				),
				// 정확히 2명만 있는 방
				chatRoom.id.in(
					queryFactory
						.select(chatRoomMember.chatRoom.id)
						.from(chatRoomMember)
						.groupBy(chatRoomMember.chatRoom.id)
						.having(chatRoomMember.count().eq(2L))
				)
			)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	// 사용자가 참여한 채팅방 조회 - lastMessageAt 기준 정렬
	@Override
	public List<ChatRoom> findActiveRoomsByUserId(Long userId) {
		return getChatRoomsByUserId(userId);
	}

	// 읽지 않은 메시지 수와 함께 조회
	@Override
	public List<ChatRoom> findRoomsWithUnreadCountByUserId(Long userId) {
		// TODO: 나중에 읽지 않은 메시지 수 계산 로직 추가 예정
		return getChatRoomsByUserId(userId);
	}

	/**
	 * 사용자의 채팅방 조회 공통 로직
	 */
	private List<ChatRoom> getChatRoomsByUserId(Long userId) {
		
		// 1:1 채팅방 조회
		List<ChatRoom> directRooms = queryFactory
			.selectFrom(chatRoom)
			.where(
				chatRoom.type.eq(ChatRoomType.DIRECT),
				chatRoom.id.in(
					queryFactory
						.select(chatRoomMember.chatRoom.id)
						.from(chatRoomMember)
						.where(chatRoomMember.user.id.eq(userId))
				)
			)
			.orderBy(chatRoom.lastMessageAt.desc().nullsLast())
			.fetch();

		// 그룹 채팅방 조회 (나가지 않은 것만)
		List<ChatRoom> groupRooms = queryFactory
			.selectFrom(chatRoom)
			.where(
				chatRoom.type.eq(ChatRoomType.GROUP),
				chatRoom.id.in(
					queryFactory
						.select(chatRoomMember.chatRoom.id)
						.from(chatRoomMember)
						.where(
							chatRoomMember.user.id.eq(userId),
							chatRoomMember.leftAt.isNull() // 나가지 않은 멤버만
						)
				)
			)
			.orderBy(chatRoom.lastMessageAt.desc().nullsLast())
			.fetch();

		// 두 결과를 합치고 lastMessageAt 기준으로 재정렬
		List<ChatRoom> result = new ArrayList<>();
		result.addAll(directRooms);
		result.addAll(groupRooms);

		// lastMessageAt 기준 정렬 (최신 메시지 순)
		result.sort(Comparator.comparing(ChatRoom::getLastMessageAt,
										 Comparator.nullsLast(Comparator.naturalOrder())).reversed());

		return result;
	}
}
