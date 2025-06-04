package org.brokong.morakbackend.global;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseDto<Void>> handleIllegalArgument(IllegalArgumentException e) {

		return ResponseEntity.badRequest()
							 .body(new ResponseDto<>(e.getMessage(), null));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ResponseDto<Void>> handleIllegalState(IllegalStateException e) {

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							 .body(new ResponseDto<>(e.getMessage(), null));
	}
}