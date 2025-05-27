package org.brokong.modakbackend.like.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import org.brokong.modakbackend.global.BaseEntity;

@Entity(name = "post_likes")
public class PostLike extends BaseEntity {

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long postId;
}
