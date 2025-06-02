package org.brokong.morakbackend.user.dto.request;

import lombok.Getter;

@Getter
public class SignupRequestDto {

	private String email;
	private String password;
	private String nickname;
}
