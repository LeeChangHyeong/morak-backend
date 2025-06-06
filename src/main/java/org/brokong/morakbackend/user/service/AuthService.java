package org.brokong.morakbackend.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.JwtUtil;
import org.brokong.morakbackend.global.ResponseDto;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.LoginType;
import org.brokong.morakbackend.user.enums.UserStatus;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // local 회원 가입
    @Transactional
    public UserResponseDto signUp(String email, String password, String nickname) {
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .pushToken("aa")
                .loginType(LoginType.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();

        try {
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace(); // 또는 log.error("회원가입 실패", e);
            throw new IllegalArgumentException("회원가입에 실패했습니다. 관리자에게 문의해주세요.");
        }


        return new UserResponseDto(user);
    }

    // 닉네임 중복 확인
    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname); // 중복이 없으면 true 반환
    }

}