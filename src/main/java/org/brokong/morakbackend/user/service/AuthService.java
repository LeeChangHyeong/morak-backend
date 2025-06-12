package org.brokong.morakbackend.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.jwt.JwtUtil;
import org.brokong.morakbackend.global.redis.RedisService;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.LoginType;
import org.brokong.morakbackend.user.enums.UserRoles;
import org.brokong.morakbackend.user.enums.UserStatus;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

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

    public UserResponseDto login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new IllegalArgumentException("차단된 사용자입니다. 관리자에게 문의해주세요.");
        }

        // JWT
        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole().name());

        // RefreshToken 발급 (UUID or JWT 가능)
        String refreshToken = UUID.randomUUID().toString();

        // Redis 저장 (key: email, value: refreshToken, 유효시간: 14일)
        redisService.setValue("refresh_token:" + user.getEmail(), refreshToken, Duration.ofDays(14));

        return UserResponseDto.from(user, accessToken, refreshToken);
    }

    public void logout(HttpServletRequest request) {
        String accessToken = jwtUtil.extractAccessToken(request);

        if (accessToken == null || !jwtUtil.validateAccessToken(accessToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        long expiration = jwtUtil.getAccessTokenExpireTime(accessToken);
        String email = jwtUtil.getEmailFromAccessToken(accessToken);

        try {
            redisService.setValue("access_token_blacklist:" + accessToken, "logout", Duration.ofMillis(expiration));
            redisService.deleteValue("refresh_token:" + email);
        } catch (Exception e) {
            throw new IllegalArgumentException("로그아웃에 실패했습니다. 관리자에게 문의해주세요.");
        }
    }
}