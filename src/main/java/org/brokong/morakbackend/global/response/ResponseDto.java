package org.brokong.morakbackend.global.response;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ResponseDto<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final Integer errorCode;
    private final LocalDateTime timestamp;

    // 성공 응답 생성자
    public ResponseDto(String message, T data) {
        this.success = true;
        this.message = message;
        this.data = data;
        this.errorCode = null;
        this.timestamp = LocalDateTime.now();
    }

    // 에러 응답 생성자
    public ResponseDto(String message, Integer errorCode) {
        this.success = false;
        this.message = message;
        this.data = null;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    // 성공 응답 팩토리 메서드
    public static <T> ResponseDto<T> success(String message, T data) {
        return new ResponseDto<>(message, data);
    }

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>("성공", data);
    }

    // 에러 응답 팩토리 메서드
    public static <T> ResponseDto<T> error(String message, Integer errorCode) {
        return new ResponseDto<>(message, errorCode);
    }
}