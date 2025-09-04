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
    // 1. 전체 게시글 - 시간순 정렬 (가장 기본적인 쿼리)
    @Index(name = "idx_post_created_at", columnList = "created_at"),
    
    // 2. 전체 게시글 - 인기순 정렬 (좋아요순 + 시간순)
    @Index(name = "idx_post_like_created", columnList = "like_count, created_at"),
    
    // 3. 전체 게시글 - 조회순 정렬 (조회순 + 시간순)
    @Index(name = "idx_post_view_created", columnList = "view_count, created_at"),
    
    // 4. 사용자별 게시글 - 시간순 정렬
    @Index(name = "idx_post_user_created", columnList = "user_id, created_at"),
    
    // 5. 사용자별 게시글 - 인기순 정렬 (필요하다면)
    // @Index(name = "idx_post_user_like_created", columnList = "user_id, like_count, created_at"),
    
    // 6. 사용자별 게시글 - 조회순 정렬 (필요하다면)  
    // @Index(name = "idx_post_user_view_created", columnList = "user_id, view_count, created_at")
})
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public void increaseViewCount() {
        this.viewCount++;
    }
}
