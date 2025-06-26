package org.brokong.morakbackend.friend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class Block extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "block_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocker_id", nullable = false)
	private User blocker; // 차단한 사람

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocked_id", nullable = false)
	private User blocked; // 차단당한 사람

	public Block(User blocker, User blocked) {
		this.blocker = blocker;
		this.blocked = blocked;
	}
}
