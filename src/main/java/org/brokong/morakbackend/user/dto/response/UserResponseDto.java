package org.brokong.morakbackend.user.dto.response;

import lombok.Getter;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.LoginType;
import org.brokong.morakbackend.user.enums.UserRoles;

import java.util.List;

@Getter
public class UserResponseDto {

    private Long id;
    private String email;
    private String nickname;
    List<Post> posts;
    private LoginType loginType;
    private UserRoles role;
    private String accessToken;
    private String refreshToken;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.posts = user.getPosts();
        this.loginType = user.getLoginType();
        this.role = user.getRole();
    }

    public UserResponseDto(User user, String accessToken, String refreshToken) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.posts = user.getPosts();
        this.loginType = user.getLoginType();
        this.role = user.getRole();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
