package org.brokong.morakbackend.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.friend.dto.response.FriendRequestResponseDto;
import org.brokong.morakbackend.friend.dto.response.FriendResponseDto;
import org.brokong.morakbackend.friend.service.FriendService;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/friends")
@Tag(name = "Friend", description = "친구 관련 API")
public class FriendController {

	private final FriendService friendService;

	@Operation(summary = "내 친구 목록 조회", description = "현재 로그인된 사용자의 친구 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "친구 목록 조회 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping
	public ResponseEntity<ResponseDto<List<FriendResponseDto>>> getMyFriends(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		List<FriendResponseDto> friends = friendService.getMyFriends(userPrincipal);
		return ResponseEntity.ok(new ResponseDto<>("친구 목록 조회 성공", friends));
	}

	@Operation(summary = "받은 친구 요청 조회", description = "현재 사용자가 받은 친구 요청 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "받은 친구 요청 조회 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping("/requests/received")
	public ResponseEntity<ResponseDto<List<FriendRequestResponseDto>>> getReceivedFriendRequests(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		List<FriendRequestResponseDto> requests = friendService.getReceivedFriendRequests(userPrincipal);
		return ResponseEntity.ok(new ResponseDto<>("받은 친구 요청 조회 성공", requests));
	}

	@Operation(summary = "보낸 친구 요청 조회", description = "현재 사용자가 보낸 친구 요청 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "보낸 친구 요청 조회 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping("/requests/sent")
	public ResponseEntity<ResponseDto<List<FriendRequestResponseDto>>> getSentFriendRequests(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		List<FriendRequestResponseDto> requests = friendService.getSentFriendRequests(userPrincipal);
		return ResponseEntity.ok(new ResponseDto<>("보낸 친구 요청 조회 성공", requests));
	}

	@Operation(summary = "친구 요청 보내기", description = "특정 사용자에게 친구 요청을 보냅니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "친구 요청 전송 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/request/{receiverId}")
	public ResponseEntity<ResponseDto<Void>> sendFriendRequest(
		@Parameter(description = "친구 요청을 받을 사용자 ID", required = true, example = "1")
		@PathVariable Long receiverId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		friendService.sendFriendRequest(userPrincipal, receiverId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("친구 요청이 정상적으로 처리되었습니다.", null));
	}

	@Operation(summary = "친구 요청 수락", description = "받은 친구 요청을 수락합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "친구 요청 수락 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/request/{requestId}/accept")
	public ResponseEntity<ResponseDto<Void>> acceptFriendRequest(
		@Parameter(description = "수락할 친구 요청 ID", required = true, example = "1")
		@PathVariable Long requestId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		friendService.acceptFriendRequest(userPrincipal, requestId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("친구 요청이 정상적으로 수락되었습니다.", null));
	}

	@Operation(summary = "친구 요청 거절", description = "받은 친구 요청을 거절합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "친구 요청 거절 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/request/{requestId}/reject")
	public ResponseEntity<ResponseDto<Void>> rejectFriendRequest(
		@Parameter(description = "거절할 친구 요청 ID", required = true, example = "1")
		@PathVariable Long requestId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		friendService.rejectFriendRequest(userPrincipal, requestId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("친구 요청이 정상적으로 거절되었습니다.", null));
	}

	@Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "친구 삭제 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/{friendId}/delete")
	public ResponseEntity<ResponseDto<Void>> deleteFriend(
		@Parameter(description = "삭제할 친구의 사용자 ID", required = true, example = "1")
		@PathVariable Long friendId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		friendService.deleteFriend(userPrincipal, friendId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("친구 삭제가 정상적으로 처리되었습니다.", null));
	}
}