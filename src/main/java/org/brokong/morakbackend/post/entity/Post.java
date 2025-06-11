package org.brokong.morakbackend.post.entity;

import jakarta.persistence.*;
import lombok.Builder;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.like.entity.PostLike;
import org.brokong.morakbackend.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
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

    public void setUser(User user) {
        this.user = user;
    }
}
