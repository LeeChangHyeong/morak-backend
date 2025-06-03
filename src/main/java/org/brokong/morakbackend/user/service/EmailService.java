package org.brokong.morakbackend.user.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.service.RedisService;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;
    private final RedisService redisService;
    // TODO: 이메일 전송 기능 구현 해야함
//    private final EmailSender emailSender;

    // 이메일 중복 확인
    public void checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    // 이메일 검증 코드 전송
    public void sendVerificationCode(String email) {

    }

    // 이메일 코드 검증
    public void verifyCode(String email, String code) {

    }

    // 이메일 검증 코드 생성
    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // 6자리 숫자
    }
}