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
    private final JavaMailSender mailSender;

    // 이메일 중복 확인
    public boolean checkEmail(String email) {
        return !userRepository.existsByEmail(email); // 중복이 없으면 true 반환
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
            log.info("[이메일 전송 성공] email: {}", email);
        } catch (Exception e) {
            log.error("[이메일 전송 실패] email: {}, message: {}", email, e.getMessage(), e);

            throw new IllegalStateException("이메일 전송에 실패했습니다. 나중에 다시 시도해주세요.");
        }

        // Redis 저장
        try {
            redisService.setValue("email_auth:" + email, authCode, Duration.ofMinutes(3));
            log.info("[Redis 저장 성공] email: {}", email);
        } catch (Exception e) {
            log.error("[Redis 저장 실패] email: {}, error: {}", email, e.getMessage(), e);
            throw new IllegalStateException("인증번호 저장에 실패했습니다. 관리자에게 문의해주세요.");
        }
    }

    // 이메일 코드 검증
    public void verifyAuthCode(String email, String authCode) {
        String key = "email_auth:" + email;
        String savedAuthCode = redisService.getValue(key);

        if(savedAuthCode == null || !savedAuthCode.equals(authCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않거나 만료되었습니다.");
        }

        // 인증 성공 시 Redis에서 삭제
        redisService.deleteValue(key);
    }

    // 이메일 검증 코드 생성
    public String createAuthCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 6자리 숫자
    }
}