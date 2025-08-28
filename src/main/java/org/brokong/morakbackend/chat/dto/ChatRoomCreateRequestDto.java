package org.brokong.morakbackend.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "채팅방 생성 요청")
public class ChatRoomCreateRequestDto {

	@Schema(description = "채팅을 시작할 친구의 사용자 ID", example = "1", required = true)
	private Long friendId;
}