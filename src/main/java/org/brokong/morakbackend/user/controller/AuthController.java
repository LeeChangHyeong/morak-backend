package org.brokong.morakbackend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.user.dto.request.LoginRequestDto;
import org.brokong.morakbackend.user.dto.request.RefreshTokenRequestDto;
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
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 회원가입을 진행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공")
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<UserResponseDto>> signUp(
        @Parameter(description = "회원가입 요청 정보", required = true)
        @RequestBody SignupRequestDto request) {

        UserResponseDto userResponseDto = authService.signUp(request.getEmail(), request.getPassword(), request.getNickname());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("회원가입 성공", userResponseDto));
    }

    @Operation(summary = "닉네임 중복 확인", description = "닉네임이 사용 가능한지 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "닉네임 중복 확인 완료"),
    })
    @GetMapping("/check-nickname")
    public ResponseEntity<ResponseDto<Boolean>> checkNickname(
        @Parameter(description = "확인할 닉네임", required = true, example = "이창형")
        @RequestParam String nickname) {

        boolean isAvailable = authService.checkNickname(nickname);

        return ResponseEntity.ok(new ResponseDto<>("닉네임 사용 가능 여부", isAvailable));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(
        @Parameter(description = "로그인 요청 정보", required = true)
        @RequestBody LoginRequestDto request) {

        LoginResponseDto loginResponseDto = authService.login(request.getEmail(), request.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("로그인 성공", loginResponseDto));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
    })
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<String>> logout(
        @Parameter(hidden = true) HttpServletRequest request) {
        authService.logout(request);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("로그아웃 성공", null));
    }

    @Operation(summary = "토큰 재발급", description = "RefreshToken을 사용하여 새로운 AccessToken을 발급받습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 RefreshToken")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<LoginResponseDto>> refreshToken(
        @Parameter(description = "토큰 재발급 요청 정보", required = true)
        @RequestBody RefreshTokenRequestDto request) {

        LoginResponseDto response = authService.refreshToken(request.getRefreshToken());
        
        return ResponseEntity.ok(new ResponseDto<>("토큰 재발급 성공", response));
    }
}