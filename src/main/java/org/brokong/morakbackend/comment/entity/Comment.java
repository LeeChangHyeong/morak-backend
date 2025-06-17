package org.brokong.morakbackend.comment.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.like.entity.CommentLike;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.user.entity.User;

@Getter
@RequiredArgsConstructor
@Entity(name = "comments")
@Table(indexes = {
    @Index(name = "idx_parent_comment_id", columnList = "parent_comment_id")
})
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
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
    }

    public void decreaseLikeCount() {
        likeCount--;
    }

    public void increaseLikeCount() {
        likeCount++;
    }
}
