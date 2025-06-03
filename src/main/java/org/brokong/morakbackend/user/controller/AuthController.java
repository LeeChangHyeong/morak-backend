package org.brokong.morakbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.ResponseDto;
import org.brokong.morakbackend.user.dto.request.LoginRequestDto;
import org.brokong.morakbackend.user.dto.request.SignupRequestDto;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.brokong.morakbackend.user.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<UserResponseDto>> signUp(@RequestBody SignupRequestDto request) {

        ResponseDto<UserResponseDto> response = new ResponseDto<>("회원가입 성공", new UserResponseDto());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<ResponseDto<String>> checkNickname(@RequestParam String nickname) {

        authService.checkNickname(nickname);

        return ResponseEntity.ok(new ResponseDto<>("사용 가능한 닉네임입니다.", null));
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
