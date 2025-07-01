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

	@Column(name = "is_active")
	private boolean isActive = true;

	@Builder
	public ChatRoomMember(ChatRoom chatRoom, User user, LocalDateTime lastReadAt) {
		this.chatRoom = chatRoom;
		this.user = user;
		this.lastReadAt = lastReadAt != null ? lastReadAt : LocalDateTime.now();
	}

	// 메시지 읽음 처리
	public void readMessage() {
		this.lastReadAt = LocalDateTime.now();
	}

	// 채팅방 나가기
	public void leaveChatRoom() {
		this.isActive = false;
	}

	// 채팅방 다시 참여
	public void rejoinChatRoom() {
		this.isActive = true;
		this.lastReadAt = LocalDateTime.now();
	}

}
