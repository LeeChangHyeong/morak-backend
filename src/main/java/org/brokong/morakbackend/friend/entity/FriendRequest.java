package org.brokong.morakbackend.friend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.friend.enums.FriendRequestStatus;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class FriendRequest extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "friend_request_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false)
	private User receiver;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FriendRequestStatus friendRequestStatus;

	public FriendRequest(User sender, User receiver) {
		this.sender = sender;
		this.receiver = receiver;
		this.friendRequestStatus = FriendRequestStatus.PENDING;
	}

	public void accept() {
		this.friendRequestStatus = FriendRequestStatus.ACCEPTED;
	}

	public void reject() {
		this.friendRequestStatus = FriendRequestStatus.REJECTED;
	}
}