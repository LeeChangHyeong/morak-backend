package org.brokong.morakbackend.chat.dto;

import lombok.Data;

@Data
public class ChatRoomMemberDto {
    private Long userId;
    private String username;
    private String email;
}
