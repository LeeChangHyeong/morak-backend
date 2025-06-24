package org.brokong.morakbackend.global.exception;

import javax.naming.AuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
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

	// 사용자 정의 예외 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ResponseDto<Void>> handleCustomException(CustomException e) {
		log.warn("❗ CustomException [{}]: {}", e.getErrorCode(), e.getMessage());
		return ResponseEntity.status(e.getHttpStatus())
							 .body(new ResponseDto<>(e.getMessage(), null));
	}

	// JWT 만료 예외 처리
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ResponseDto<Void>> handleExpiredJwtException(ExpiredJwtException e) {
		log.warn("❗ 만료된 JWT 토큰: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							 .body(new ResponseDto<>("만료된 JWT 토큰입니다. 다시 로그인해주세요.", null));
	}

	// JWT 관련 예외 처리
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ResponseDto<Void>> handleJwtException(JwtException e) {
		log.warn("❗ JWT 토큰 오류: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							 .body(new ResponseDto<>("유효하지 않은 JWT 토큰입니다.", null));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ResponseDto<Void>> handleAuthenticationException(AuthenticationException e) {
		log.warn("❗ 인증 실패: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							 .body(new ResponseDto<>("인증이 필요합니다. 로그인 후 다시 시도해주세요.", null));
	}

	@ExceptionHandler(InsufficientAuthenticationException.class)
	public ResponseEntity<ResponseDto<Void>> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
		log.warn("❗ 인증 정보 부족: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							 .body(new ResponseDto<>("로그인이 필요한 서비스입니다.", null));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ResponseDto<Void>> handleBadCredentialsException(BadCredentialsException e) {
		log.warn("❗ 잘못된 자격 증명: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							 .body(new ResponseDto<>("이메일 또는 비밀번호가 올바르지 않습니다.", null));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseDto<Void>> handleAccessDeniedException(AccessDeniedException e) {
		log.warn("❗ 접근 권한 없음: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
							 .body(new ResponseDto<>("해당 작업을 수행할 권한이 없습니다.", null));
	}

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

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ResponseDto<Void>> handleRuntimeException(RuntimeException e) {
		log.error("❗ 런타임 예외: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							 .body(new ResponseDto<>("처리 중 오류가 발생했습니다.", null));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseDto<Void>> handleValidationException(MethodArgumentNotValidException e) {
		log.warn("❗ 유효성 검증 실패: {}", e.getMessage());
		String errorMessage = e.getBindingResult().getFieldErrors().stream()
							   .map(error -> error.getField() + ": " + error.getDefaultMessage())
							   .findFirst()
							   .orElse("입력값이 올바르지 않습니다.");

		return ResponseEntity.badRequest()
							 .body(new ResponseDto<>(errorMessage, null));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ResponseDto<Void>> handleMissingParameter(MissingServletRequestParameterException e) {
		log.warn("❗ 필수 파라미터 누락: {}", e.getMessage());
		return ResponseEntity.badRequest()
							 .body(new ResponseDto<>("필수 파라미터가 누락되었습니다: " + e.getParameterName(), null));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ResponseDto<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
		log.warn("❗ 파라미터 타입 불일치: {}", e.getMessage());
		return ResponseEntity.badRequest()
							 .body(new ResponseDto<>("파라미터 형식이 올바르지 않습니다: " + e.getName(), null));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ResponseDto<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
		log.warn("❗ 지원하지 않는 HTTP 메서드: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
							 .body(new ResponseDto<>("지원하지 않는 HTTP 메서드입니다.", null));
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ResponseDto<Void>> handleNoHandlerFound(NoHandlerFoundException e) {
		log.warn("❗ 핸들러를 찾을 수 없음: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
							 .body(new ResponseDto<>("요청한 리소스를 찾을 수 없습니다.", null));
	}

	@ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleException(Exception e) {
		log.error("❗ 예상치 못한 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							 .body(new ResponseDto<>("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", null));
    }
}
