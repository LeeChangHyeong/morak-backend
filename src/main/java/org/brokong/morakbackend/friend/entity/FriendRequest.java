package org.brokong.morakbackend.friend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.brokong.morakbackend.friend.enums.FriendRequestStatus;
import org.brokong.morakbackend.global.BaseEntity;

@Entity(name = "friend_requests")
public class FriendRequest extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "friend_request_id")
	private Long id;

	@Column(nullable = false)
	private Long senderId;

	@Column(nullable = false)
	private Long receiverId;

	@Column(nullable = false)
	private FriendRequestStatus friendRequeststatus;
}
