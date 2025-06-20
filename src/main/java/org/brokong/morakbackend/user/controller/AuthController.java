package org.brokong.morakbackend.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.user.dto.request.LoginRequestDto;
import org.brokong.morakbackend.user.dto.request.SignupRequestDto;
import org.brokong.morakbackend.user.dto.response.LoginResponseDto;
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

        UserResponseDto userResponseDto = authService.signUp(request.getEmail(), request.getPassword(), request.getNickname());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("회원가입 성공", userResponseDto));
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<ResponseDto<Boolean>> checkNickname(@RequestParam String nickname) {

        boolean isAvailable = authService.checkNickname(nickname);

        return ResponseEntity.ok(new ResponseDto<>("닉네임 사용 가능 여부", isAvailable));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(@RequestBody LoginRequestDto request) {

        LoginResponseDto loginResponseDto = authService.login(request.getEmail(), request.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("로그인 성공", loginResponseDto));
    }
//
//    // 카카오 소셜 로그인
//    @PostMapping("/login/kakao")
//    public ResponseEntity<ResponseDto<UserResponseDto>> kakaoLogin() {
//        ResponseDto<UserResponseDto> response = new ResponseDto<>("로그인 성공", new UserResponseDto());
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//
//    // 애플 소셜 로그인
//    @PostMapping("/login/apple")
//    public ResponseEntity<ResponseDto<UserResponseDto>> appleLogin() {
//        ResponseDto<UserResponseDto> response = new ResponseDto<>("로그인 성공", new UserResponseDto());
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<String>> logout(HttpServletRequest request) {
        authService.logout(request);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("로그아웃 성공", null));
    }
}
