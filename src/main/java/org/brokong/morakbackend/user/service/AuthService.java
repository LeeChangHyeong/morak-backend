package org.brokong.morakbackend.user.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.JwtUtil;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 회원 가입
    public void signUp(String email, String password, String nickname) {

    }

    // 닉네임 중복 확인
    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
    }

}