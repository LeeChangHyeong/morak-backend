package org.brokong.morakbackend.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.global.enums.ErrorCode;
import org.brokong.morakbackend.global.exception.CustomException;
import org.brokong.morakbackend.global.jwt.JwtUtil;
import org.brokong.morakbackend.global.redis.RedisKey;
import org.brokong.morakbackend.global.redis.RedisService;
import org.brokong.morakbackend.user.dto.response.LoginResponseDto;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.LoginType;
import org.brokong.morakbackend.user.enums.UserRoles;
import org.brokong.morakbackend.user.enums.UserStatus;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final RedisService redisService;

	// local 회원 가입
	@Transactional
	public UserResponseDto signUp(String email, String password, String nickname) {
		// 디버그 로그 추가
		log.info("회원가입 요청 - 이메일: '{}', 비밀번호: '{}', 닉네임: '{}'", email, password, nickname);

		// 이메일 중복 확인
		if (userRepository.existsByEmail(email)) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		// 닉네임 중복 확인
		if (userRepository.existsByNickname(nickname)) {
			throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}

		String encodedPassword = passwordEncoder.encode(password);

		User user = User.builder()
						.email(email)
						.password(encodedPassword)
						.nickname(nickname)
						.pushToken("test")
						.loginType(LoginType.LOCAL)
						.status(UserStatus.ACTIVE)
						.role(UserRoles.USER)
						.build();

		try {
			userRepository.save(user);
		} catch (Exception e) {
			log.error("회원가입 실패 - 이메일: {}, 에러: {}", email, e.getMessage());
			throw new CustomException(ErrorCode.DATABASE_ERROR);
		}

		return UserResponseDto.from(user);
	}

	// 닉네임 중복 확인
	public boolean checkNickname(String nickname) {
		return !userRepository.existsByNickname(nickname); // 중복이 없으면 true 반환
	}

	public LoginResponseDto login(String email, String password) {

		User user = userRepository.findByEmail(email)
								  .orElseThrow(() -> {
									  log.warn("로그인 실패 - 존재하지 않는 이메일: {}", email);
									  return new CustomException(ErrorCode.INVALID_CREDENTIALS);
								  });

		if (!passwordEncoder.matches(password, user.getPassword())) {
			log.warn("로그인 실패 - 비밀번호 불일치: {}", email);
			throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
		}

		if (user.getStatus() == UserStatus.BLOCKED) {
			log.warn("로그인 실패 - 차단된 사용자: {}", email);
			throw new CustomException(ErrorCode.ACCOUNT_SUSPENDED);
		}

		if (user.getStatus() == UserStatus.WITHDRAWN) {
			log.warn("로그인 실패 - 탈퇴한 사용자: {}", email);
			throw new CustomException(ErrorCode.ACCOUNT_DELETED);
		}

		// JWT
		String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole().name(), user.getNickname(), user.getId());

		// JWT RefreshToken 발급
		String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getId());

		// Redis 저장 (key: email, value: refreshToken, 유효시간: 14일)
		try {
			redisService.setValue(RedisKey.refreshTokenKey(user.getEmail()), refreshToken, Duration.ofDays(14));
		} catch (Exception e) {
			log.error("Redis 저장 실패 - 이메일: {}, 에러: {}", email, e.getMessage());
		}

		return LoginResponseDto.from(user, accessToken, refreshToken);
	}

	public void logout(HttpServletRequest request) {
		String accessToken = jwtUtil.extractAccessToken(request);

		// 토큰 존재 여부 확인
		if (accessToken == null) {
			throw new CustomException(ErrorCode.TOKEN_INVALID);
		}

		// 토큰 유효성 검증
		if (!jwtUtil.validateAccessToken(accessToken)) {
			throw new CustomException(ErrorCode.TOKEN_INVALID);
		}

		long expiration = jwtUtil.getAccessTokenExpireTime(accessToken);
		String email = jwtUtil.getEmailFromAccessToken(accessToken);

		try {
			// 엑세스 토큰 블랙리스트 추가
			redisService.setValue(RedisKey.accessTokenBlacklistKey(accessToken), "logout", Duration.ofMillis(expiration));
			redisService.deleteValue(RedisKey.refreshTokenKey(email));

			log.info("로그아웃 성공 - 이메일: {}", email);
		} catch (Exception e) {
			log.error("로그아웃 실패 - 이메일: {}, 에러: {}", email, e.getMessage());
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	// 토큰 재발급
	@Transactional
	public LoginResponseDto refreshToken(String refreshToken) {
		// 1. RefreshToken 유효성 검증
		if (!jwtUtil.validateRefreshToken(refreshToken)) {
			log.warn("토큰 재발급 실패 - 유효하지 않은 RefreshToken");
			throw new CustomException(ErrorCode.TOKEN_INVALID);
		}

		// 2. RefreshToken에서 이메일 추출
		String email;
		try {
			email = jwtUtil.getEmailFromRefreshToken(refreshToken);
		} catch (Exception e) {
			log.warn("토큰 재발급 실패 - RefreshToken에서 이메일 추출 실패");
			throw new CustomException(ErrorCode.TOKEN_INVALID);
		}

		// 3. 사용자 조회
		User user = userRepository.findByEmail(email)
								  .orElseThrow(() -> {
									  log.warn("토큰 재발급 실패 - 존재하지 않는 사용자: {}", email);
									  return new CustomException(ErrorCode.USER_NOT_FOUND);
								  });

		// 4. 사용자 상태 확인
		if (user.getStatus() == UserStatus.BLOCKED) {
			log.warn("토큰 재발급 실패 - 차단된 사용자: {}", email);
			throw new CustomException(ErrorCode.ACCOUNT_SUSPENDED);
		}

		if (user.getStatus() == UserStatus.WITHDRAWN) {
			log.warn("토큰 재발급 실패 - 탈퇴한 사용자: {}", email);
			throw new CustomException(ErrorCode.ACCOUNT_DELETED);
		}

		// 5. Redis에 저장된 RefreshToken과 일치하는지 확인
		String storedRefreshToken;
		try {
			storedRefreshToken = redisService.getValue(RedisKey.refreshTokenKey(email));
		} catch (Exception e) {
			log.error("Redis 조회 실패 - 이메일: {}", email);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		if (!refreshToken.equals(storedRefreshToken)) {
			log.warn("토큰 재발급 실패 - RefreshToken 불일치: {}", email);
			throw new CustomException(ErrorCode.TOKEN_INVALID);
		}

		// 6. 새로운 토큰들 생성
		String newAccessToken = jwtUtil.createAccessToken(
			user.getEmail(),
			user.getRole().name(),
			user.getNickname(),
			user.getId()
		);

		String newRefreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getId());

		// 7. Redis 업데이트
		try {
			redisService.setValue(
				RedisKey.refreshTokenKey(user.getEmail()),
				newRefreshToken,
				Duration.ofDays(14)
			);
		} catch (Exception e) {
			log.error("토큰 재발급 시 Redis 업데이트 실패 - 이메일: {}", email);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		log.info("토큰 재발급 성공 - 이메일: {}", email);
		return LoginResponseDto.from(user, newAccessToken, newRefreshToken);
	}
}