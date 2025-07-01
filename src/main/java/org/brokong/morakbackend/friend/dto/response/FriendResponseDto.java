package org.brokong.morakbackend.friend.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.brokong.morakbackend.friend.entity.Friend;

@Getter
@Builder
public class FriendResponseDto {
    
    private Long friendId;
    private Long userId;
    private String nickname;
    private String email;
    
    public static FriendResponseDto from(Friend friend, Long currentUserId) {
        // 현재 사용자가 sender면 receiver 정보를, receiver면 sender 정보를 반환
        boolean isCurrentUserSender = friend.getSender().getId().equals(currentUserId);
        
        if (isCurrentUserSender) {
            return FriendResponseDto.builder()
                    .friendId(friend.getId())
                    .userId(friend.getReceiver().getId())
                    .nickname(friend.getReceiver().getNickname())
                    .email(friend.getReceiver().getEmail())
                    .build();
        } else {
            return FriendResponseDto.builder()
                    .friendId(friend.getId())
                    .userId(friend.getSender().getId())
                    .nickname(friend.getSender().getNickname())
                    .email(friend.getSender().getEmail())
                    .build();
        }
    }
}
