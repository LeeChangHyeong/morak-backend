package org.brokong.morakbackend.global.response;

import java.time.format.DateTimeFormatter;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ResponseDto<T> {

	private final boolean success;
	private final String message;
	private final T data;
	private final Integer errorCode;
	private final String timestamp; // ISO 8601 형식

	// ISO 8601 포맷터 (예: 2025-09-26T15:30:45.123)
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public ResponseDto(String message, T data) {
		this.success = true;
		this.message = message;
		this.data = data;
		this.errorCode = null;
		this.timestamp = LocalDateTime.now().format(FORMATTER);
	}

	public ResponseDto(String message, Integer errorCode) {
		this.success = false;
		this.message = message;
		this.data = null;
		this.errorCode = errorCode;
		this.timestamp = LocalDateTime.now().format(FORMATTER);
	}

	// 성공 응답 팩토리 메서드
	public static <T> ResponseDto<T> success(String message, T data) {
		return new ResponseDto<>(message, data);
	}

	// 에러 응답 팩토리 메서드
	public static <T> ResponseDto<T> error(String message, Integer errorCode) {
		return new ResponseDto<>(message, errorCode);
	}
}