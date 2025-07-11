package org.brokong.morakbackend.chat.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.dto.ChatRoomCreateRequestDto;
import org.brokong.morakbackend.chat.service.ChatRoomService;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	@PostMapping
	public ResponseEntity<ResponseDto<Void>> createChatRoom(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody ChatRoomCreateRequestDto chatRoomCreateRequestDto) {

		// 채팅방 생성
		chatRoomService.createChatRoom(userPrincipal, chatRoomCreateRequestDto);

		return ResponseEntity.ok(new ResponseDto<>("채팅방이 정상적으로 생성되었습니다.", null));
	}
}
