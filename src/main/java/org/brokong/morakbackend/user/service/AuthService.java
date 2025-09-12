package org.brokong.morakbackend.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
			throw new IllegalArgumentException("회원가입에 실패했습니다. 관리자에게 문의해주세요.");
		}

		return UserResponseDto.from(user);
	}

	// 닉네임 중복 확인
	public boolean checkNickname(String nickname) {
		return !userRepository.existsByNickname(nickname); // 중복이 없으면 true 반환
	}

	public LoginResponseDto login(String email, String password) {

		User user = userRepository.findByEmail(email)
								  .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		if (user.getStatus() == UserStatus.BLOCKED) {
			throw new IllegalArgumentException("차단된 사용자입니다. 관리자에게 문의해주세요.");
		}

		// JWT
		String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole().name(), user.getNickname(), user.getId());

		// JWT RefreshToken 발급
		String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getId());

		// Redis 저장 (key: email, value: refreshToken, 유효시간: 14일)
		redisService.setValue(RedisKey.refreshTokenKey(user.getEmail()), refreshToken, Duration.ofDays(14));



		return LoginResponseDto.from(user, accessToken, refreshToken);
	}

	public void logout(HttpServletRequest request) {
		String accessToken = jwtUtil.extractAccessToken(request);

		if (accessToken == null || !jwtUtil.validateAccessToken(accessToken)) {
			throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
		}

		long expiration = jwtUtil.getAccessTokenExpireTime(accessToken);
		String email = jwtUtil.getEmailFromAccessToken(accessToken);

		try {
			redisService.setValue(RedisKey.accessTokenBlacklistKey(accessToken), "logout", Duration.ofMillis(expiration));
			redisService.deleteValue(RedisKey.refreshTokenKey(email));
		} catch (Exception e) {
			throw new IllegalArgumentException("로그아웃에 실패했습니다. 관리자에게 문의해주세요.");
		}
	}

	// 토큰 재발급
	@Transactional
	public LoginResponseDto refreshToken(String refreshToken) {
		// 1. RefreshToken 유효성 검증
		if (!jwtUtil.validateRefreshToken(refreshToken)) {
			throw new IllegalArgumentException("유효하지 않은 RefreshToken입니다.");
		}

		// 2. RefreshToken에서 이메일 추출
		String email = jwtUtil.getEmailFromRefreshToken(refreshToken);

		// 3. 사용자 조회 (단일 DB 쿼리)
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// 4. 사용자 상태 확인
		if (user.getStatus() == UserStatus.BLOCKED) {
			throw new IllegalArgumentException("차단된 사용자입니다.");
		}

		if (user.getStatus() == UserStatus.WITHDRAWN) {
			throw new IllegalArgumentException("탈퇴한 사용자입니다.");
		}

		// 5. Redis에 저장된 RefreshToken과 일치하는지 확인
		String storedRefreshToken = redisService.getValue(RedisKey.refreshTokenKey(email));
		if (!refreshToken.equals(storedRefreshToken)) {
			throw new IllegalArgumentException("유효하지 않은 RefreshToken입니다.");
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
		redisService.setValue(RedisKey.refreshTokenKey(user.getEmail()), newRefreshToken, Duration.ofDays(14));

		return LoginResponseDto.from(user, newAccessToken, newRefreshToken);
	}
}