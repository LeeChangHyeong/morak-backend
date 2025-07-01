package org.brokong.morakbackend.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoomMember extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_room_member_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id", nullable = false)
	private ChatRoom chatRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "last_read_at")
	private LocalDateTime lastReadAt;

	// isActive 제거하고 leftAt 추가
	@Column(name = "left_at")
	private LocalDateTime leftAt; // null = 아직 안 나감, 값 있음 = 나간 시점

	@Builder
	public ChatRoomMember(ChatRoom chatRoom, User user, LocalDateTime lastReadAt) {
		this.chatRoom = chatRoom;
		this.user = user;
		this.lastReadAt = lastReadAt != null ? lastReadAt : LocalDateTime.now();
		this.leftAt = null; // 처음에는 나가지 않은 상태
	}

	// 메시지 읽음 처리
	public void readMessage() {
		this.lastReadAt = LocalDateTime.now();
	}

	// 그룹 채팅방 나가기 (1:1 채팅에서는 사용 안함)
	public void leaveGroupChatRoom() {
		this.leftAt = LocalDateTime.now();
	}

	// 그룹 채팅방 다시 참여 (재초대 시)
	public void rejoinGroupChatRoom() {
		this.leftAt = null;
		this.lastReadAt = LocalDateTime.now();
	}

	// 현재 활성 멤버인지 확인 (나가지 않았는지)
	public boolean isActiveMember() {
		return this.leftAt == null;
	}

	// 1:1 채팅인지 그룹 채팅인지에 따라 다르게 처리
	public boolean canReceiveMessage() {
		// 1:1 채팅: 항상 메시지 받을 수 있음 (차단 기능은 별도)
		// 그룹 채팅: 나가지 않은 경우만 메시지 받을 수 있음
		return isActiveMember();
	}
}