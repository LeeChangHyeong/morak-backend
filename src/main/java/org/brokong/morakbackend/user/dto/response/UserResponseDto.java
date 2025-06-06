package org.brokong.morakbackend.user.dto.response;

import lombok.Getter;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.UserRoles;

@Getter
public class UserResponseDto {

	private String email;
	private String nickname;
	private UserRoles role;
	private String accessToken;
	private String refreshToken;

	public UserResponseDto(User user) {
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		this.role = user.getRole();
	}

	public UserResponseDto(User user, String accessToken, String refreshToken) {
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		this.role = user.getRole();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
