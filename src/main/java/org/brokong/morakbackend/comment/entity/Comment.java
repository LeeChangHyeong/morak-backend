package org.brokong.morakbackend.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.brokong.morakbackend.global.BaseEntity;

@Entity(name = "comments")
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "comment_id")
	private Long id;

	@Column(nullable = false)
	private String postId;

	@Column(nullable = false)
	private String userId;

	@Column
	private String parentCommentId;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private long likeCount;

}
