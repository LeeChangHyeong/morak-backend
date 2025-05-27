package org.brokong.modakbackend.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.brokong.modakbackend.global.BaseEntity;

@Entity(name = "comments")
public class Comment extends BaseEntity {

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
