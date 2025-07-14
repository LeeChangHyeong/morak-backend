package org.brokong.morakbackend.chat.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.chat.dto.ChatRoomCreateRequestDto;
import org.brokong.morakbackend.chat.dto.ChatRoomDetailResponseDto;
import org.brokong.morakbackend.chat.dto.ChatRoomMemberDto;
import org.brokong.morakbackend.chat.dto.ChatRoomSummaryResponseDto;
import org.brokong.morakbackend.chat.entity.ChatRoom;
import org.brokong.morakbackend.chat.entity.ChatRoomMember;
import org.brokong.morakbackend.chat.enums.ChatRoomType;
import org.brokong.morakbackend.chat.repository.ChatRoomRepository;
import org.brokong.morakbackend.friend.repository.FriendRepository;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public void createChatRoom(UserPrincipal userPrincipal, ChatRoomCreateRequestDto chatRoomCreateRequestDto) {

        Long userId = userPrincipal.getId();
        Long friendId = chatRoomCreateRequestDto.getFriendId();

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("자기 자신과는 채팅방을 생성할 수 없습니다.");
        }

        // 두 유저로 이미 존재하는 채팅방 조회
        Optional<ChatRoom> existingRoom = chatRoomRepository
                .findDirectChatRoomByUserIds(userId, friendId, ChatRoomType.DIRECT);

        if (existingRoom.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 채팅방입니다.");
        }

        // 없으면 새 방 생성 (유저 조회 포함)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 친구 관계 확인
        friendRepository.findByUsers(user, friend)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계를 찾을 수 없습니다."));

        ChatRoom newRoom = ChatRoom.builder()
                .type(ChatRoomType.DIRECT)
                .build();

        ChatRoomMember member1 = new ChatRoomMember(newRoom, user, LocalDateTime.now());
        ChatRoomMember member2 = new ChatRoomMember(newRoom, friend, LocalDateTime.now());

        newRoom.getMembers().add(member1);
        newRoom.getMembers().add(member2);

        chatRoomRepository.save(newRoom);
    }

    public List<ChatRoomSummaryResponseDto> getChatRoomsByUser(UserPrincipal userPrincipal) {
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ChatRoom> rooms = chatRoomRepository.findAllByMembersUserId(userPrincipal.getId())
                .orElse(List.of());

        return rooms.stream().map(room -> {
            ChatRoomSummaryResponseDto dto = new ChatRoomSummaryResponseDto();
            dto.setRoomId(room.getId());
            dto.setRoomName(room.getDisplayName(currentUser)); // 메소드 활용
            dto.setLastMessageTime(room.getLastMessageAt());
            dto.setLastMessage(room.getLastMessage());
            dto.setRoomType(room.getType());
            return dto;
        }).collect(Collectors.toList());
    }

    public ChatRoomDetailResponseDto getChatRoomDetail(UserPrincipal userPrincipal, Long roomId) {
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 멤버 확인
        boolean isMember = chatRoom.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userPrincipal.getId()));

        if (!isMember) {
            throw new IllegalArgumentException("채팅방에 참여하지 않은 사용자입니다.");
        }

        ChatRoomDetailResponseDto dto = new ChatRoomDetailResponseDto();
        dto.setRoomId(chatRoom.getId());
        dto.setRoomName(chatRoom.getDisplayName(currentUser)); // 메소드 활용
        dto.setRoomType(chatRoom.getType());
        dto.setCreatedAt(chatRoom.getCreatedAt());
        dto.setLastMessageAt(chatRoom.getLastMessageAt());
        dto.setLastMessage(chatRoom.getLastMessage());

        List<ChatRoomMemberDto> members = chatRoom.getMembers().stream()
                .map(this::convertToMemberDto)
                .collect(Collectors.toList());
        dto.setMembers(members);

        return dto;
    }

    private ChatRoomMemberDto convertToMemberDto(ChatRoomMember member) {
        ChatRoomMemberDto dto = new ChatRoomMemberDto();
        dto.setUserId(member.getUser().getId());
        dto.setUsername(member.getUser().getNickname());
        dto.setEmail(member.getUser().getEmail());

        return dto;
    }
}
