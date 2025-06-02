package org.brokong.morakbackend.user.controller;

import java.util.List;
import org.brokong.morakbackend.global.ResponseDto;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

	// 단일 유저 조회
	@GetMapping("/{userId}")
	public ResponseEntity<ResponseDto<UserResponseDto>> getUser(@PathVariable Long userId) {

		ResponseDto<UserResponseDto> response = new ResponseDto<>("단일 사용자 조회 성공", new UserResponseDto());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// 다중 유저 조회
	@GetMapping("/batch")
	public ResponseEntity<ResponseDto<List<UserResponseDto>>> getUsersByIds(@RequestParam List<Long> userIds) {

		ResponseDto<List<UserResponseDto>> response = new ResponseDto<>("다중 사용자 조회 성공", List.of(new UserResponseDto()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// 전체 유저 조회
	@GetMapping
	public ResponseEntity<ResponseDto<List<UserResponseDto>>> getAllUsers() {
		ResponseDto<List<UserResponseDto>> response = new ResponseDto<>("전체 사용자 조회 성공", List.of(new UserResponseDto()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
