package org.brokong.morakbackend.friend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.brokong.morakbackend.global.BaseEntity;

@Entity(name = "friends")
public class Friend extends BaseEntity {

	@Column(nullable = false)
	private Long senderId;

	@Column(nullable = false)
	private Long receiverId;
}
