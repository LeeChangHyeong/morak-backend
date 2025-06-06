package org.brokong.morakbackend.user.dto.response;

import lombok.Getter;
import org.brokong.morakbackend.user.entity.User;

@Getter
public class UserResponseDto {

	private String email;
	private String nickname;

	public UserResponseDto(User user) {
		this.email = user.getEmail();
		this.nickname = user.getNickname();
	}
}
