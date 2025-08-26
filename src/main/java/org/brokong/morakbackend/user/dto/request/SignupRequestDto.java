package org.brokong.morakbackend.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회원가입 요청")
public class SignupRequestDto {

	@Schema(description = "이메일", example = "user@example.com", required = true)
	private String email;

	@Schema(description = "비밀번호", example = "password123", required = true)
	private String password;

	@Schema(description = "닉네임", example = "사용자123", required = true)
	private String nickname;
}