package org.brokong.morakbackend.user.dto.response;

import lombok.Getter;
import org.brokong.morakbackend.post.dto.PostResponseDto;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.LoginType;
import org.brokong.morakbackend.user.enums.UserRoles;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private List<PostResponseDto> posts;
    private LoginType loginType;
    private UserRoles role;
    private String accessToken;
    private String refreshToken;

    private UserResponseDto() {
        // 기본 생성자는 private 으로 막아둠
    }

    public static UserResponseDto from(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.id = user.getId();
        dto.email = user.getEmail();
        dto.nickname = user.getNickname();
        dto.posts = user.getPosts().stream()
                        .map(PostResponseDto::from)
                        .collect(Collectors.toList());
        dto.loginType = user.getLoginType();
        dto.role = user.getRole();
        return dto;
    }

    public static UserResponseDto from(User user, String accessToken, String refreshToken) {
        UserResponseDto dto = from(user); // 위의 from(User)를 재사용
        dto.accessToken = accessToken;
        dto.refreshToken = refreshToken;
        return dto;
    }
}