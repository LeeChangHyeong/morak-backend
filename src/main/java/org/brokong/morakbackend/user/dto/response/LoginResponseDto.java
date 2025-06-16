package org.brokong.morakbackend.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.LoginType;
import org.brokong.morakbackend.user.enums.UserRoles;

@Getter
@NoArgsConstructor
public class LoginResponseDto {
	private Long id;
	private String email;
	private String nickname;
	private LoginType loginType;
	private UserRoles role;
	private String accessToken;
	private String refreshToken;

	public static LoginResponseDto from(User user, String accessToken, String refreshToken) {
		LoginResponseDto dto = new LoginResponseDto();
		dto.id = user.getId();
		dto.email = user.getEmail();
		dto.nickname = user.getNickname();
		dto.loginType = user.getLoginType();
		dto.role = user.getRole();
		dto.accessToken = accessToken;
		dto.refreshToken = refreshToken;

		return dto;
	}
}
