package org.brokong.morakbackend.global.exception;

import javax.naming.AuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.global.enums.ErrorCode;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;



@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 사용자 정의 예외 처리 (ErrorCode 사용)
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ResponseDto<Void>> handleCustomException(CustomException e) {
		if (e.getErrorCode() != null) {
			log.warn("❗ CustomException [{}]: {}", e.getErrorCode().getCode(), e.getMessage());
			return ResponseEntity
				.status(e.getHttpStatus())
				.body(ResponseDto.error(e.getMessage(), e.getErrorCode().getCode()));
		} else {
			// 기존 방식 호환성
			log.warn("❗ CustomException: {}", e.getMessage());
			return ResponseEntity
				.status(e.getHttpStatus())
				.body(ResponseDto.error(e.getMessage(), e.getCode()));
		}
	}

	// JWT 만료 예외 처리
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ResponseDto<Void>> handleExpiredJwtException(ExpiredJwtException e) {
		log.warn("❗ 만료된 JWT 토큰: {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.TOKEN_EXPIRED;
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ResponseDto.error(errorCode.getMessage(), errorCode.getCode()));
	}

	// JWT 관련 예외 처리
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ResponseDto<Void>> handleJwtException(JwtException e) {
		log.warn("❗ JWT 토큰 오류: {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.TOKEN_INVALID;
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ResponseDto.error(errorCode.getMessage(), errorCode.getCode()));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ResponseDto<Void>> handleAuthenticationException(AuthenticationException e) {
		log.warn("❗ 인증 실패: {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED_ACCESS;
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ResponseDto.error(errorCode.getMessage(), errorCode.getCode()));
	}

	@ExceptionHandler(InsufficientAuthenticationException.class)
	public ResponseEntity<ResponseDto<Void>> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
		log.warn("❗ 인증 정보 부족: {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED_ACCESS;
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ResponseDto.error("로그인이 필요한 서비스입니다.", errorCode.getCode()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ResponseDto<Void>> handleBadCredentialsException(BadCredentialsException e) {
		log.warn("❗ 잘못된 자격 증명: {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.INVALID_CREDENTIALS;
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ResponseDto.error(errorCode.getMessage(), errorCode.getCode()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseDto<Void>> handleAccessDeniedException(AccessDeniedException e) {
		log.warn("❗ 접근 권한 없음: {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED_ACCESS;
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ResponseDto.error(errorCode.getMessage(), errorCode.getCode()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseDto<Void>> handleIllegalArgument(IllegalArgumentException e) {
		log.warn("❗ IllegalArgumentException: {}", e.getMessage());
		return ResponseEntity.badRequest()
							 .body(ResponseDto.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ResponseDto<Void>> handleIllegalState(IllegalStateException e) {
		log.error("❗ IllegalStateException: {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ResponseDto.error(errorCode.getMessage(), errorCode.getCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseDto<Void>> handleValidationException(MethodArgumentNotValidException e) {
		log.warn("❗ 유효성 검증 실패: {}", e.getMessage());
		String errorMessage = e.getBindingResult().getFieldErrors().stream()
							   .map(error -> error.getField() + ": " + error.getDefaultMessage())
							   .findFirst()
							   .orElse("입력값이 올바르지 않습니다.");

		return ResponseEntity.badRequest()
							 .body(ResponseDto.error(errorMessage, HttpStatus.BAD_REQUEST.value()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDto<Void>> handleException(Exception e) {
		log.error("❗ 예상치 못한 오류 발생", e);
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ResponseDto.error(errorCode.getMessage(), errorCode.getCode()));
	}
}