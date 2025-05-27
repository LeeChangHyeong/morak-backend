package org.brokong.morakbackend.like.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.brokong.morakbackend.global.BaseEntity;

@Entity(name = "comment_likes")
public class CommentLike extends BaseEntity {

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long commentId;

}
