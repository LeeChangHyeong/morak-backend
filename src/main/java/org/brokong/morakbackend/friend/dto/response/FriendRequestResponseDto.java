package org.brokong.morakbackend.friend.dto.response;

import static org.brokong.morakbackend.global.DateTimeFormatters.MORAK_DATETIME_FORMATTER;

import lombok.Builder;
import lombok.Getter;
import org.brokong.morakbackend.friend.entity.FriendRequest;
import org.brokong.morakbackend.friend.enums.FriendRequestStatus;

@Getter
@Builder
public class FriendRequestResponseDto {
    
    private Long requestId;
    private Long senderId;
    private String senderNickname;
    private String senderEmail;
    private Long receiverId;
    private String receiverNickname;
    private String receiverEmail;
    private FriendRequestStatus status;
    private String createdAt;
    
    public static FriendRequestResponseDto from(FriendRequest friendRequest) {
        return FriendRequestResponseDto.builder()
                .requestId(friendRequest.getId())
                .senderId(friendRequest.getSender().getId())
                .senderNickname(friendRequest.getSender().getNickname())
                .senderEmail(friendRequest.getSender().getEmail())
                .receiverId(friendRequest.getReceiver().getId())
                .receiverNickname(friendRequest.getReceiver().getNickname())
                .receiverEmail(friendRequest.getReceiver().getEmail())
                .status(friendRequest.getFriendRequestStatus())
                .createdAt(friendRequest.getCreatedAt().format(MORAK_DATETIME_FORMATTER))
                .build();
    }
}
