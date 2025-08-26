package org.brokong.morakbackend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.user.dto.request.EmailVerifyRequestDto;
import org.brokong.morakbackend.user.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/email")
@Tag(name = "Email", description = "이메일 인증 관련 API")
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "이메일 중복 확인", description = "이메일이 사용 가능한지 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이메일 중복 확인 완료")
    })
    @GetMapping("/check-email")
    public ResponseEntity<ResponseDto<Boolean>> checkEmail(
        @Parameter(description = "확인할 이메일", required = true, example = "user@example.com")
        @RequestParam String email) {

        boolean isAvailable = emailService.checkEmail(email);
        return ResponseEntity.ok(new ResponseDto<>("이메일 사용 가능 여부", isAvailable));
    }

    @Operation(summary = "이메일 인증번호 전송", description = "입력된 이메일로 인증번호를 전송합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "이메일 인증번호 전송 성공")
    })
    @PostMapping("/send-email")
    public ResponseEntity<ResponseDto<Void>> sendEmail(
        @Parameter(description = "인증번호를 받을 이메일", required = true, example = "user@example.com")
        @RequestParam String email) {

        emailService.sendAuthCode(email);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ResponseDto<>("이메일 인증번호 전송 성공", null));
    }

    @Operation(summary = "이메일 인증번호 확인", description = "전송된 인증번호를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이메일 인증번호 확인 성공")
    })
    @PostMapping("/verify-email")
    public ResponseEntity<ResponseDto<Void>> verifyEmail(
        @Parameter(description = "이메일 인증 요청 정보", required = true)
        @RequestBody EmailVerifyRequestDto requestDto) {

        emailService.verifyAuthCode(requestDto.getEmail(), requestDto.getCode());
        return ResponseEntity.status(HttpStatus.OK)
                             .body(new ResponseDto<>("이메일 인증번호 확인 성공", null));
    }
}