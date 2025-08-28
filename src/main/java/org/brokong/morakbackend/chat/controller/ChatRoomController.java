package org.brokong.morakbackend.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ChatRoom", description = "채팅방 관련 API")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "1:1 채팅방 생성", description = "특정 친구와의 1:1 채팅방을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "채팅방 생성 성공")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping
    public ResponseEntity<ResponseDto<Void>> createChatRoom(
        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
        @Parameter(description = "채팅방 생성 요청 정보", required = true)
        @RequestBody ChatRoomCreateRequestDto chatRoomCreateRequestDto) {

        chatRoomService.createChatRoom(userPrincipal, chatRoomCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("채팅방이 정상적으로 생성되었습니다.", null));
    }

    @Operation(summary = "내 채팅방 목록 조회", description = "현재 사용자가 참여 중인 채팅방 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping
    public ResponseEntity<ResponseDto<List<ChatRoomSummaryResponseDto>>> getAllChatRooms(
        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<ChatRoomSummaryResponseDto> chatRooms = chatRoomService.getChatRoomsByUser(userPrincipal);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("채팅방 목록을 정상적으로 조회하였습니다.", chatRooms));
    }

    @Operation(summary = "채팅방 상세 조회", description = "특정 채팅방의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "채팅방 상세 정보 조회 성공")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/{roomId}")
    public ResponseEntity<ResponseDto<ChatRoomDetailResponseDto>> getChatRoom(
        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
        @Parameter(description = "조회할 채팅방 ID", required = true, example = "1")
        @PathVariable Long roomId) {

        ChatRoomDetailResponseDto chatRoom = chatRoomService.getChatRoomDetail(userPrincipal, roomId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("채팅방 정보를 정상적으로 조회했습니다.", chatRoom));
    }
}