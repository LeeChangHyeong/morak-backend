package org.brokong.morakbackend.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.user.entity.User;

@Getter
@NoArgsConstructor
@Entity
public class PostReport extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_report_id")
	private Long id;

	// 신고한 유저
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 신고 대상 게시글
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	// 신고 사유
	@Column(nullable = false)
	private String reason;

	public PostReport(User user, Post post, String reason) {
		this.user = user;
		this.post = post;
		this.reason = reason;
	}
}
