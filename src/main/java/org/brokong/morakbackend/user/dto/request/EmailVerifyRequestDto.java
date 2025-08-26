package org.brokong.morakbackend.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "이메일 인증 요청")
public class EmailVerifyRequestDto {

	@Schema(description = "이메일", example = "user@example.com", required = true)
	private String email;

	@Schema(description = "인증번호", example = "123456", required = true)
	private String code;

}
