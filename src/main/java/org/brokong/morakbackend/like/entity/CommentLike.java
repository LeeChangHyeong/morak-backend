package org.brokong.morakbackend.like.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.brokong.morakbackend.global.BaseEntity;

@Entity(name = "comment_likes")
public class CommentLike extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "comment_like_id")
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long commentId;

}
