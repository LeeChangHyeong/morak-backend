package org.brokong.morakbackend.user.controller;

import org.brokong.morakbackend.global.ResponseDto;
import org.brokong.morakbackend.user.dto.request.LoginRequestDto;
import org.brokong.morakbackend.user.dto.request.SignupRequestDto;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<UserResponseDto>> signUp(@RequestBody SignupRequestDto request) {

        ResponseDto<UserResponseDto> response = new ResponseDto<>("회원가입 성공", new UserResponseDto());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<UserResponseDto>> login(@RequestBody LoginRequestDto request) {

        ResponseDto<UserResponseDto> response = new ResponseDto<>("로그인 성공", new UserResponseDto());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 카카오 소셜 로그인
    @PostMapping("/login/kakao")
    public ResponseEntity<ResponseDto<UserResponseDto>> kakaoLogin() {
        ResponseDto<UserResponseDto> response = new ResponseDto<>("카카오 로그인 성공", new UserResponseDto());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 애플 소셜 로그인
    @PostMapping("/login/apple")
    public ResponseEntity<ResponseDto<UserResponseDto>> appleLogin() {
        ResponseDto<UserResponseDto> response = new ResponseDto<>("애플 로그인 성공", new UserResponseDto());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<String>> logout() {
        ResponseDto<String> response = new ResponseDto<>("로그아웃 성공", "로그아웃 되었습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
