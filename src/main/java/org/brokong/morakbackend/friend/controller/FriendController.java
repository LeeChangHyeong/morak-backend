package org.brokong.morakbackend.friend.controller;

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
public class FriendController {

	private final FriendService friendService;

	/**
	 * 내 친구 목록 조회
	 */
	@GetMapping
	public ResponseEntity<ResponseDto<List<FriendResponseDto>>> getMyFriends(
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		List<FriendResponseDto> friends = friendService.getMyFriends(userPrincipal);
		return ResponseEntity.ok(new ResponseDto<>("친구 목록 조회 성공", friends));
	}

	/**
	 * 받은 친구 요청 목록 조회
	 */
	@GetMapping("/requests/received")
	public ResponseEntity<ResponseDto<List<FriendRequestResponseDto>>> getReceivedFriendRequests(
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		List<FriendRequestResponseDto> requests = friendService.getReceivedFriendRequests(userPrincipal);
		return ResponseEntity.ok(new ResponseDto<>("받은 친구 요청 조회 성공", requests));
	}

	/**
	 * 보낸 친구 요청 목록 조회
	 */
	@GetMapping("/requests/sent")
	public ResponseEntity<ResponseDto<List<FriendRequestResponseDto>>> getSentFriendRequests(
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		List<FriendRequestResponseDto> requests = friendService.getSentFriendRequests(userPrincipal);
		return ResponseEntity.ok(new ResponseDto<>("보낸 친구 요청 조회 성공", requests));
	}

	@PostMapping("/request/{receiverId}")
	public ResponseEntity<ResponseDto<Void>> sendFriendRequest(
		@PathVariable Long receiverId,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {

		friendService.sendFriendRequest(userPrincipal, receiverId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("친구 요청이 정상적으로 처리되었습니다.", null));
	}

	@PostMapping("/request/{requestId}/accept")
	public ResponseEntity<ResponseDto<Void>> acceptFriendRequest(
		@PathVariable Long requestId,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {

		friendService.acceptFriendRequest(userPrincipal, requestId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("친구 요청이 정상적으로 수락되었습니다.", null));
	}

	@PostMapping("/request/{requestId}/reject")
	public ResponseEntity<ResponseDto<Void>> rejectFriendRequest(
		@PathVariable Long requestId,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {

		friendService.rejectFriendRequest(userPrincipal, requestId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("친구 요청이 정상적으로 거절되었습니다.", null));
	}

	@PostMapping("/{friendId}/delete")
	public ResponseEntity<ResponseDto<Void>> deleteFriend(
		@PathVariable Long friendId,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {

		friendService.deleteFriend(userPrincipal, friendId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("친구 삭제가 정상적으로 처리되었습니다.", null));
	}
}
