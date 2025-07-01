package org.brokong.morakbackend.chat.entity;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.chat.enums.MessageType;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_message_id")
	private Long id;

	@JoinColumn(name = "chat_room_id", nullable = false)
	private Long chatRoomId;

	@JoinColumn(name = "sender_id", nullable = false)
	private String senderNickname;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MessageType type;

	@Column(name = "is_read")
	private boolean isRead = false;

	@Builder
	public ChatMessage(Long chatRoomId, String senderNickname, String message, MessageType type) {
		this.chatRoomId = chatRoomId;
		this.senderNickname = senderNickname;
		this.message = message;
		this.type = type;
	}

	// 읽음 처리
	public void markAsRead() {
		this.isRead = true;
	}

}
