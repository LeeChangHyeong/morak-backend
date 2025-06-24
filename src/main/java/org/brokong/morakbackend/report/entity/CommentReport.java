package org.brokong.morakbackend.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.comment.entity.Comment;
import org.brokong.morakbackend.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class CommentReport {

	@Id
	@GeneratedValue
	@Column(name = "post_report_id")
	private Long id;

	// 신고한 유저
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 신고 대상 게시글
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", nullable = false)
	private Comment comment;

	// 신고 사유
	@Column(nullable = false)
	private String reason;

	public CommentReport(User user, Comment comment, String reason) {
		this.user = user;
		this.comment = comment;
		this.reason = reason;
	}
}
