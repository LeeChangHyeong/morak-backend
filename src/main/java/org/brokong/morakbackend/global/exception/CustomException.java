package org.brokong.morakbackend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final String errorCode;

	public CustomException(HttpStatus httpStatus, String errorCode, String message) {
		super(message);
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
	}

	public CustomException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
		this.errorCode = httpStatus.name();
	}
}