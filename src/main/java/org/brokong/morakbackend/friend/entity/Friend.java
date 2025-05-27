package org.brokong.morakbackend.friend.entity;

import jakarta.persistence.*;
import org.brokong.morakbackend.global.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

@Entity(name = "friends")
public class Friend extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "friend_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false)
	private User receiver;
}
