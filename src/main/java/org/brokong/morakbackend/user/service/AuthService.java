package org.brokong.morakbackend.user.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.JwtUtil;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.LoginType;
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

    // local 회원 가입
    public void signUp(String email, String password, String nickname) {
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .pushToken("")
                .loginType(LoginType.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("회원가입에 실패했습니다. 관리자에게 문의해주세요.");
        }
    }

    // 닉네임 중복 확인
    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
    }

}