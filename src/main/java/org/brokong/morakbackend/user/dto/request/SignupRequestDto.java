package org.brokong.morakbackend.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원가입 요청")
public class SignupRequestDto {

	@Schema(description = "이메일", example = "user@example.com", required = true)
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자 이내여야 합니다.")
	private String email;

	@Schema(description = "비밀번호 (8-20자, 영문+숫자+특수문자)", example = "password123!", required = true)
	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
	@Pattern(
		regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
		message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
	)
	private String password;

	@Schema(description = "닉네임 (2-10자, 한글/영문/숫자)", example = "사용자123", required = true)
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
	@Pattern(
		regexp = "^[a-zA-Z0-9가-힣]+$",
		message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다."
	)
	private String nickname;
}