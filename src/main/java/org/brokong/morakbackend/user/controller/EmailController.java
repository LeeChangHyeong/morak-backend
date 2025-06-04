package org.brokong.morakbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.ResponseDto;
import org.brokong.morakbackend.user.dto.request.EmailVerifyReqeustDto;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.brokong.morakbackend.user.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/email")
public class EmailController {

    private final EmailService emailService;


    // 이메일 중복 확인
    @GetMapping("check-email")
    public ResponseEntity<ResponseDto> checkEmail(@RequestParam String email) {
        ResponseDto<UserResponseDto> response = new ResponseDto<>("회원가입 성공", new UserResponseDto());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 이메일 인증번호 전송
    @PostMapping("send-email")
    public ResponseEntity<ResponseDto<Void>> sendEmail(@RequestParam String email) {
        emailService.sendAuthCode(email);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ResponseDto<>("이메일 인증번호 전송 성공", null));
    }

    // 이메일 인증번호 확인
    @PostMapping("verify-email")
    public ResponseEntity<ResponseDto> verifyEmail(@RequestBody EmailVerifyReqeustDto requestDto) {
        ResponseDto<UserResponseDto> response = new ResponseDto<>("이메일 인증번호 확인 성공", new UserResponseDto());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
