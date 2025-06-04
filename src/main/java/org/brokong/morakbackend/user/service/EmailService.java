package org.brokong.morakbackend.user.service;

import java.time.Duration;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.global.service.RedisService;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;
    private final RedisService redisService;
    // TODO: 이메일 전송 기능 구현 해야함
    private final JavaMailSender mailSender;

    // 이메일 중복 확인
    public void checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    // 이메일 검증 코드 전송
    public void sendAuthCode(String email) {
        String authCode = createAuthCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[모락(morak)] 이메일 인증 코드입니다.");
        message.setText("인증번호: " + authCode + "\n인증번호를 입력창에 입력해주세요.");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("[이메일 전송 실패] email: {}, message: {}", email, e.getMessage(), e);

            throw new IllegalStateException("이메일 전송에 실패했습니다. 나중에 다시 시도해주세요.");
        }

        // 성공 시 Redis 저장
        redisService.setValues("email_auth:" + email, authCode, Duration.ofMinutes(3));
    }

    // 이메일 코드 검증
    public void verifyCode(String email, String authCode) {

    }

    // 이메일 검증 코드 생성
    public String createAuthCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 6자리 숫자
    }
}