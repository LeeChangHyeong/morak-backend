package org.brokong.morakbackend.chat.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.brokong.morakbackend.chat.enums.ChatRoomType;
import org.brokong.morakbackend.global.entity.BaseEntity;
import org.brokong.morakbackend.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column
    private String name; // 그룹일때만

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType type;

    // 그룹 채팅에만 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creater_id")
    private User creater; // GROUP 타입일때만 사용

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt; // 마지막 메시지 시간

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomMember> members = new ArrayList<>();


    @Builder
    public ChatRoom(String name, ChatRoomType type, User creater) {
        this.name = name; // DIRECT일 때는 null
        this.type = type;
        this.creater = creater; // DIRECT일 때는 null
        this.lastMessageAt = LocalDateTime.now();
    }

    // 다이렉트 채팅방의 경우 현재 사용자 기준으로 상대방 이름 반환
    public String getDisplayName(User currentUser) {
        if (type == ChatRoomType.DIRECT) {
            return members.stream()
                    .filter(member -> !member.getUser().getId().equals(currentUser.getId()))
                    .findFirst()
                    .map(member -> member.getUser().getNickname())
                    .orElse("Unknown User");
        }
        // 그룹 채팅의 경우 설정된 이름 반환
        return name != null ? name : "Group Chat";
    }

    // 새 메시지가 올 때마다 업데이트
    public void updateLastMessageTime(String message) {
        this.lastMessage = message;
        this.lastMessageAt = LocalDateTime.now();
    }

}
