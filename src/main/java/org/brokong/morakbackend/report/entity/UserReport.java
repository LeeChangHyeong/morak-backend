package org.brokong.morakbackend.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

@Entity
@NoArgsConstructor
public class UserReport extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_report_id")
	private Long id;

	// 신고한 유저
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 신고 당한 유저
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_user_id", nullable = false)
	private User targetUser;

	// 신고 사유
	@Column(nullable = false)
	private String reason;

	public UserReport(User user, User targetUser, String reason) {
		this.user = user;
		this.targetUser = targetUser;
		this.reason = reason;
	}
}
