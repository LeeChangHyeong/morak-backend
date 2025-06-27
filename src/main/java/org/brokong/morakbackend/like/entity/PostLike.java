package org.brokong.morakbackend.like.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.comment.entity.Comment;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.user.entity.User;

@NoArgsConstructor
@Entity
@Table(indexes = {
    @Index(name = "idx_post_like_user_post", columnList = "user_id, post_id", unique = true)
})
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

    @Builder
    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
