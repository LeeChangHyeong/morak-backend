package org.brokong.morakbackend.user.dto.request;

import lombok.Getter;

@Getter
public class EmailVerifyReqeustDto {

	private String email;
	private String code;
}
