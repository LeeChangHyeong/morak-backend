package org.brokong.morakbackend.friend.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.friend.service.FriendService;
import org.brokong.morakbackend.global.Security.UserPrincipal;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/friends")
public class FriendController {

	private final FriendService friendService;

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
}
