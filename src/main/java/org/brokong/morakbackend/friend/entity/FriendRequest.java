package org.brokong.morakbackend.friend.entity;

import jakarta.persistence.*;
import org.brokong.morakbackend.friend.enums.FriendRequestStatus;
import org.brokong.morakbackend.global.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

@Entity(name = "friend_requests")
public class FriendRequest extends BaseEntity {

	@Id
	@GeneratedValue
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
	private FriendRequestStatus friendRequeststatus;
}