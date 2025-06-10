package org.brokong.morakbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.brokong.morakbackend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ResponseDto<UserResponseDto>> getMyInfo() {
        UserResponseDto myInfo = userService.getMyInfo();

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("본인 정보 조회 성공", myInfo));
    }

    // 단일 유저 조회 by Id
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUserById(@PathVariable Long userId) {
        UserResponseDto user = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("유저 정보 조회 성공", user));
    }

    // 단일 유저 조회 by nickname
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUserByNickname(@PathVariable String nickname) {
        UserResponseDto user = userService.getUserByNickname(nickname);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("유저 정보 조회 성공", user));
    }

    // 유저 검색
    @GetMapping("/search")
    public ResponseEntity<ResponseDto<List<UserResponseDto>>> searchUsersByNickname(@RequestParam String nickname) {

        List<UserResponseDto> users = userService.searchUsersByNickname(nickname);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("닉네임에 해당하는 유저가 없습니다.", users));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("유저 검색 성공", users));
    }

    // 다중 유저 조회
//    @GetMapping("/batch")
//    public ResponseEntity<ResponseDto<List<UserResponseDto>>> getUsersByIds(@RequestParam List<Long> userIds) {
//
//        ResponseDto<List<UserResponseDto>> response = new ResponseDto<>("다중 사용자 조회 성공", List.of(new UserResponseDto()));
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    // 전체 유저 조회
    // 관리자 페이지
    @PreAuthorize("hasRole('ADMIN')")
   @GetMapping
   public ResponseEntity<ResponseDto<List<UserResponseDto>>> getAllUsers() {

       List<UserResponseDto> users = userService.getAllUsers();

       return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("전체 유저 조회 성공", users));
   }

    // 유저 신고
    @PostMapping("/{userId}/report")
    public ResponseEntity<ResponseDto<String>> reportUser(@PathVariable Long userId) {
        ResponseDto<String> response = new ResponseDto<>("사용자 신고 성공", "User reported successfully");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 유저 탈퇴
    @PostMapping("/withdrawal")
    public ResponseEntity<ResponseDto<String>> withdrawal() {
        ResponseDto<String> response = new ResponseDto<>("회원탈퇴 성공", "회원탈퇴 되었습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // TODO: 관리자 계정만
    // 유저 차단
    @PostMapping("{userId}/block")
    public ResponseEntity<ResponseDto<String>> blockUser(@PathVariable Long userId) {
        ResponseDto<String> response = new ResponseDto<>("유저 차단 성공", "유저가 차단되었습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
