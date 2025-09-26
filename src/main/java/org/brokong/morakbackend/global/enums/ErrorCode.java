package org.brokong.morakbackend.global.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
	// 인증 관련 (4000번대)
	INVALID_CREDENTIALS(4001, "이메일 또는 비밀번호가 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
	TOKEN_EXPIRED(4002, "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
	TOKEN_INVALID(4003, "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED_ACCESS(4004, "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
	EMAIL_NOT_VERIFIED(4005, "이메일 인증이 필요합니다", HttpStatus.UNAUTHORIZED),
	ACCOUNT_SUSPENDED(4006, "계정이 정지되었습니다", HttpStatus.FORBIDDEN),
	ACCOUNT_DELETED(4007, "삭제된 계정입니다", HttpStatus.GONE),

	// 사용자 관련 (4100번대)
	USER_NOT_FOUND(4101, "사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
	EMAIL_ALREADY_EXISTS(4102, "이미 사용중인 이메일입니다.", HttpStatus.CONFLICT),
	INVALID_EMAIL_FORMAT(4103, "이메일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
	INVALID_PASSWORD_FORMAT(4104, "비밀번호는 8자 이상이어야 합니다", HttpStatus.BAD_REQUEST),
	NICKNAME_ALREADY_EXISTS(4105, "이미 사용중인 닉네임입니다", HttpStatus.CONFLICT),
	INVALID_PROFILE_IMAGE(4106, "프로필 이미지 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),

	// 게시물 관련 (4200번대)
	POST_NOT_FOUND(4201, "게시물을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
	POST_ACCESS_DENIED(4202, "게시물에 접근할 권한이 없습니다", HttpStatus.FORBIDDEN),
	POST_CONTENT_TOO_LONG(4203, "게시물 내용이 너무 깁니다", HttpStatus.BAD_REQUEST),
	INVALID_IMAGE_FORMAT(4204, "지원하지 않는 이미지 형식입니다", HttpStatus.BAD_REQUEST),
	IMAGE_SIZE_TOO_LARGE(4205, "이미지 크기가 너무 큽니다", HttpStatus.BAD_REQUEST),
	TOO_MANY_IMAGES(4206, "이미지는 최대 10개까지 업로드 가능합니다", HttpStatus.BAD_REQUEST),
	POST_ALREADY_DELETED(4207, "이미 삭제된 게시물입니다", HttpStatus.GONE),

	// 댓글 관련 (4300번대)
	COMMENT_NOT_FOUND(4301, "댓글을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
	COMMENT_ACCESS_DENIED(4302, "댓글에 접근할 권한이 없습니다", HttpStatus.FORBIDDEN),
	COMMENT_TOO_LONG(4303, "댓글이 너무 깁니다", HttpStatus.BAD_REQUEST),
	COMMENT_ALREADY_DELETED(4304, "이미 삭제된 댓글입니다", HttpStatus.GONE),

	// 팔로우 관련 (4400번대)
	ALREADY_FOLLOWING(4401, "이미 팔로우하고 있습니다", HttpStatus.CONFLICT),
	NOT_FOLLOWING(4402, "팔로우하고 있지 않습니다", HttpStatus.BAD_REQUEST),
	CANNOT_FOLLOW_SELF(4403, "자기 자신을 팔로우할 수 없습니다", HttpStatus.BAD_REQUEST),
	FOLLOW_LIMIT_EXCEEDED(4404, "팔로우 한도를 초과했습니다", HttpStatus.BAD_REQUEST),
	BLOCKED_USER(4405, "차단된 사용자입니다", HttpStatus.FORBIDDEN),

	// 좋아요 관련 (4500번대)
	ALREADY_LIKED(4501, "이미 좋아요를 누른 게시물입니다", HttpStatus.CONFLICT),
	NOT_LIKED(4502, "좋아요를 누르지 않은 게시물입니다", HttpStatus.BAD_REQUEST),
	CANNOT_LIKE_OWN_POST(4503, "자신의 게시물에는 좋아요를 누를 수 없습니다", HttpStatus.BAD_REQUEST),

	// Rate Limiting (4900번대)
	TOO_MANY_REQUESTS(4901, "너무 많은 요청을 보냈습니다. 잠시 후 다시 시도해주세요", HttpStatus.TOO_MANY_REQUESTS),
	POST_LIMIT_EXCEEDED(4902, "하루 게시물 작성 한도를 초과했습니다", HttpStatus.TOO_MANY_REQUESTS),
	COMMENT_LIMIT_EXCEEDED(4903, "댓글 작성 한도를 초과했습니다", HttpStatus.TOO_MANY_REQUESTS),

	// 서버 에러 (5000번대)
	INTERNAL_SERVER_ERROR(5001, "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
	DATABASE_ERROR(5002, "데이터베이스 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
	EXTERNAL_API_ERROR(5003, "외부 API 호출 중 오류가 발생했습니다", HttpStatus.BAD_GATEWAY),
	FILE_STORAGE_ERROR(5004, "파일 저장소 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

	private final int code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(int code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}
}