package org.brokong.morakbackend.like.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.brokong.morakbackend.global.BaseEntity;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.user.entity.User;

@Entity(name = "post_likes")
public class PostLike extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "post_like_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;
}
