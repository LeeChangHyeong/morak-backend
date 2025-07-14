package org.brokong.morakbackend.chat.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.dto.ChatRoomCreateRequestDto;
import org.brokong.morakbackend.chat.dto.ChatRoomDetailResponseDto;
import org.brokong.morakbackend.chat.dto.ChatRoomSummaryResponseDto;
import org.brokong.morakbackend.chat.service.ChatRoomService;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> createChatRoom(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody ChatRoomCreateRequestDto chatRoomCreateRequestDto) {

        // 채팅방 생성
        chatRoomService.createChatRoom(userPrincipal, chatRoomCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("채팅방이 정상적으로 생성되었습니다.", null));
    }

    // 본인 채팅방 목록
    @GetMapping
    public ResponseEntity<ResponseDto<List<ChatRoomSummaryResponseDto>>> getAllChatRooms(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ChatRoomSummaryResponseDto> chatRooms = chatRoomService.getChatRoomsByUser(userPrincipal);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("채팅방 목록을 정상적으로 조회하였습니다.", chatRooms));
    }

    // 채팅방 단일 조회 (특정 채팅방 상세 정보)
    @GetMapping("/{roomId}")
    public ResponseEntity<ResponseDto<ChatRoomDetailResponseDto>> getChatRoom(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                              @PathVariable Long roomId) {
        ChatRoomDetailResponseDto chatRoom = chatRoomService.getChatRoomDetail(userPrincipal, roomId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("채팅방 정보를 정상적으로 조회했습니다.", chatRoom));
    }


}
