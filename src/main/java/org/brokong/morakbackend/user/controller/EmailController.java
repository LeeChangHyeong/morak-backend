package org.brokong.morakbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.user.dto.request.EmailVerifyReqeustDto;
import org.brokong.morakbackend.user.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/email")
public class EmailController {

    private final EmailService emailService;

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<ResponseDto<Boolean>> checkEmail(@RequestParam String email) {
        boolean isAvailable = emailService.checkEmail(email);

        return ResponseEntity.ok(new ResponseDto<>("이메일 사용 가능 여부", isAvailable));
    }

    // 이메일 인증번호 전송
    @PostMapping("/send-email")
    public ResponseEntity<ResponseDto<Void>> sendEmail(@RequestParam String email) {
        emailService.sendAuthCode(email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto<>("이메일 인증번호 전송 성공", null));
    }

    // 이메일 인증번호 확인
    @PostMapping("/verify-email")
    public ResponseEntity<ResponseDto> verifyEmail(@RequestBody EmailVerifyReqeustDto requestDto) {
        emailService.verifyAuthCode(requestDto.getEmail(), requestDto.getCode());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>("이메일 인증번호 확인 성공", null));
    }
}
