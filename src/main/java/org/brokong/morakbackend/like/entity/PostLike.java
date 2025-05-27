package org.brokong.morakbackend.like.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.brokong.morakbackend.global.BaseEntity;

@Entity(name = "post_likes")
public class PostLike extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "post_like_id")
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long postId;
}
