package org.brokong.morakbackend.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.brokong.morakbackend.global.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

@Entity(name = "posts")
public class Post extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "post_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Long viewCount;

	@Column(nullable = false)
	private Long likeCount;

}
