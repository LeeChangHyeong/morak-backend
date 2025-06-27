package org.brokong.morakbackend.post.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.like.entity.PostLike;
import org.brokong.morakbackend.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(indexes = {
    @Index(name = "idx_post_user_id", columnList = "user_id"),
    @Index(name = "idx_post_created_at", columnList = "created_at"),
    @Index(name = "idx_post_like_count", columnList = "like_count"),
    @Index(name = "idx_post_view_count", columnList = "view_count")
})
public class Post extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long likeCount = 0L;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @Builder
    public Post(User user, String content) {
        this.user = user;
        this.content = content;
        // user.addPost(this); 제거 - User 엔티티의 OneToMany 제거로 불필요
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
