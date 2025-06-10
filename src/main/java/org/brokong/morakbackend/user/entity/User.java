package org.brokong.morakbackend.user.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.user.enums.LoginType;
import org.brokong.morakbackend.user.enums.UserRoles;
import org.brokong.morakbackend.user.enums.UserStatus;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@Entity(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    // 읽기 전용
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    @Column(nullable = false)
    private String pushToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoles role;


    @Builder
    public User(String email, String password, String nickname, LoginType loginType, String pushToken, UserStatus status, UserRoles role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.loginType = loginType;
        this.pushToken = pushToken;
        this.status = status;
        this.role = role;
    }


    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }

    public void block() {
        this.status = UserStatus.BLOCKED;
    }

    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;

        // 특정 못하게 개인정보 파기
        this.email = DigestUtils.sha256Hex(this.email + UUID.randomUUID());
        this.nickname = "탈퇴한 사용자";
    }
}
