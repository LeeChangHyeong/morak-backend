package org.brokong.modakbackend.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.brokong.modakbackend.global.BaseEntity;

@Entity(name = "posts")
public class Post extends BaseEntity {

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Long viewCount;

	@Column(nullable = false)
	private Long likeCount;

}
