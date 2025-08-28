package org.brokong.morakbackend.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.friend.service.BlockService;
import org.brokong.morakbackend.global.security.UserPrincipal;
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
@Tag(name = "Block", description = "사용자 차단 관련 API")
public class BlockController {

	private final BlockService blockService;

	@Operation(summary = "사용자 차단", description = "특정 사용자를 차단합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "사용자 차단 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/{blockedUserId}")
	public ResponseEntity<ResponseDto<Void>> blockUser(
		@Parameter(description = "차단할 사용자 ID", required = true, example = "1")
		@PathVariable Long blockedUserId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		blockService.blockUser(userPrincipal, blockedUserId);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("사용자 차단에 성공했습니다.", null));
	}

	@Operation(summary = "사용자 차단 해제", description = "차단된 사용자의 차단을 해제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "차단 해제 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/{blockedUserId}/cancel")
	public ResponseEntity<ResponseDto<Void>> unblockUser(
		@Parameter(description = "차단 해제할 사용자 ID", required = true, example = "1")
		@PathVariable Long blockedUserId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		blockService.unblockUser(userPrincipal, blockedUserId);
		return ResponseEntity.ok(new ResponseDto<>("차단을 해제했습니다.", null));
	}
}