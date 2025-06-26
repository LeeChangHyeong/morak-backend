package org.brokong.morakbackend.friend.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.friend.service.BlockService;
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
@RequestMapping("${api.prefix}/blocks")
public class BlockController {

	private final BlockService blockService;

	@PostMapping("/{blockedUserId}")
	public ResponseEntity<ResponseDto<Void>> blockUser(
		@PathVariable Long blockedUserId,
		@AuthenticationPrincipal UserPrincipal userPrincipal
		) {
		blockService.blockUser(userPrincipal, blockedUserId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("사용자 차단에 성공했습니다.", null));
	}

	@PostMapping("/{blockedUserId}/cancel")
	public ResponseEntity<ResponseDto<Void>> unblockUser(
		@PathVariable Long blockedUserId,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		blockService.unblockUser(userPrincipal, blockedUserId);
		return ResponseEntity.ok(new ResponseDto<>("차단을 해제했습니다.", null));
	}

}
