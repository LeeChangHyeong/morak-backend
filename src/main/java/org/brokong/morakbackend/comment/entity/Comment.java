package org.brokong.morakbackend.comment.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.like.entity.CommentLike;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.user.entity.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)  // 👈 추가!
@Table(indexes = {
    @Index(name = "idx_parent_comment_id", columnList = "parent_comment_id")
})
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)  // ✅ Post 삭제 시 Comment도 자동 삭제
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private long likeCount;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // modifiedAt 추가 - 내용 수정만 추적
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @Builder
    public Comment(Post post, User user, Comment parentComment, String content, long likeCount) {
        this.post = post;
        this.user = user;
        this.parentComment = parentComment;
        this.content = content;
        this.likeCount = likeCount;
    }

    public void delete() {
        this.isDeleted = true;
        this.content = "삭제된 댓글입니다.";
    }

    public void updateContent(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }

    public void decreaseLikeCount() {
        likeCount--;
    }

    public void increaseLikeCount() {
        likeCount++;
    }
}
