package org.brokong.morakbackend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.global.request.ReportRequestDto;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.brokong.morakbackend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "본인 정보 조회 성공")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<UserResponseDto>> getMyInfo(
        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UserResponseDto myInfo = userService.getMyInfo(userPrincipal);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("본인 정보 조회 성공", myInfo));
    }

    @Operation(summary = "사용자 조회 (ID)", description = "사용자 ID로 특정 사용자 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUserById(
        @Parameter(description = "사용자 ID", required = true, example = "1")
        @PathVariable Long userId) {

        UserResponseDto user = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("유저 정보 조회 성공", user));
    }

    @Operation(summary = "사용자 조회 (닉네임)", description = "닉네임으로 특정 사용자 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공")
    })
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUserByNickname(
        @Parameter(description = "사용자 닉네임", required = true, example = "이창형")
        @PathVariable String nickname) {

        UserResponseDto user = userService.getUserByNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("유저 정보 조회 성공", user));
    }

    @Operation(summary = "사용자 검색", description = "닉네임으로 사용자를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유저 검색 완료")
    })
    @GetMapping("/search")
    public ResponseEntity<ResponseDto<List<UserResponseDto>>> searchUsersByNickname(
        @Parameter(description = "검색할 닉네임", required = true, example = "창형")
        @RequestParam String nickname) {

        List<UserResponseDto> users = userService.searchUsersByNickname(nickname);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("닉네임에 해당하는 유저가 없습니다.", users));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("유저 검색 성공", users));
    }

    @Operation(summary = "전체 사용자 조회 (관리자)", description = "관리자 권한으로 전체 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "전체 유저 조회 성공")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ResponseDto<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("전체 유저 조회 성공", users));
    }

    @Operation(summary = "사용자 신고", description = "특정 사용자를 신고합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "사용자 신고 성공")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/{userId}/report")
    public ResponseEntity<ResponseDto<String>> reportUser(
        @Parameter(description = "신고할 사용자 ID", required = true, example = "1")
        @PathVariable Long userId,
        @Parameter(description = "신고 요청 정보", required = true)
        @RequestBody ReportRequestDto requestDto,
        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

        userService.reportUser(userId, requestDto, userPrincipal);
        ResponseDto<String> response = new ResponseDto<>("사용자 신고 성공", "유저가 정상적으로 신고되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 탈퇴 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원탈퇴 성공")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/withdrawal")
    public ResponseEntity<ResponseDto<String>> withdrawal(
        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

        userService.withdrawal(userPrincipal);
        ResponseDto<String> response = new ResponseDto<>("회원탈퇴 성공", "회원탈퇴 되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "사용자 차단 (관리자)", description = "관리자 권한으로 특정 사용자를 차단합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유저 차단 성공")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/block")
    public ResponseEntity<ResponseDto<String>> blockUser(
        @Parameter(description = "차단할 사용자 ID", required = true, example = "1")
        @PathVariable Long userId) {

        userService.blockUserById(userId);
        ResponseDto<String> response = new ResponseDto<>("유저 차단 성공", "유저가 차단되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}