package org.brokong.morakbackend.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("❗ IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ResponseDto<>(e.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseDto<Void>> handleIllegalState(IllegalStateException e) {
        log.error("❗ IllegalStateException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto<>(e.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleException(Exception e) {
        log.error("❗ Unhandled Exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto<>("서버 내부 오류가 발생했습니다.", null));
    }
}