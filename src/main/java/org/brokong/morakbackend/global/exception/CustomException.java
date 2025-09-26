package org.brokong.morakbackend.global.exception;

import lombok.Getter;
import org.brokong.morakbackend.global.enums.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;
	private final HttpStatus httpStatus;
	private final int code;

	// ErrorCode를 사용하는 생성자
	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getHttpStatus();
		this.code = errorCode.getCode();
	}

}