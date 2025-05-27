package org.brokong.morakbackend.friend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.brokong.morakbackend.global.BaseEntity;

@Entity(name = "friends")
public class Friend extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "friend_id")
	private Long id;

	@Column(nullable = false)
	private Long senderId;

	@Column(nullable = false)
	private Long receiverId;
}
