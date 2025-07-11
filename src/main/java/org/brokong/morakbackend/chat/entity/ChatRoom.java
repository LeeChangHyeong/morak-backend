package org.brokong.morakbackend.chat.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.chat.enums.ChatRoomType;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_room_id")
	private Long id;

	@Column
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ChatRoomType type;

	// 그룹 채팅에만 사용
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creater_id")
	private User creater; // GROUP 타입일때만 사용

	@Column(name = "last_message_at")
	private LocalDateTime lastMessageAt; // 마지막 메시지 시간

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatRoomMember> members = new ArrayList<>();


	@Builder
	public ChatRoom(String name, ChatRoomType type, User creater) {
		this.name = name;
		this.type = type;
		this.creater = creater; // DIRECT일 때는 null
		this.lastMessageAt = LocalDateTime.now();
	}

	// 새 메시지가 올 때마다 업데이트
	public void updateLastMessageTime() {
		this.lastMessageAt = LocalDateTime.now();
	}

}
