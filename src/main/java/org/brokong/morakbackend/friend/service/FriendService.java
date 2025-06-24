package org.brokong.morakbackend.friend.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.friend.entity.Friend;
import org.brokong.morakbackend.friend.entity.FriendRequest;

import org.brokong.morakbackend.friend.repository.FriendRepository;
import org.brokong.morakbackend.friend.repository.FriendRequestRepository;
import org.brokong.morakbackend.global.Security.UserPrincipal;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendService {

	private final UserRepository userRepository;
	private final FriendRequestRepository friendRequestRepository;
	private final FriendRepository friendRepository;

	@Transactional
	public void sendFriendRequest(UserPrincipal userPrincipal, Long receiverId) {
		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		// 자기 자신에게 요청 방지
		if (user.getId().equals(receiverId)) {
			throw new IllegalArgumentException("자기 자신에게는 친구 요청을 할 수 없습니다.");
		}
		// 중복 요청 방지 - 양방향 모두 확인
		boolean alreadyRequested = friendRequestRepository
									   .findBySenderAndReceiver(user, receiver).isPresent()
								   || friendRequestRepository.findBySenderAndReceiver(receiver, user).isPresent();

		if (alreadyRequested) {
			throw new IllegalArgumentException("이미 친구 요청이 존재합니다.");
		}

		FriendRequest friendRequest = new FriendRequest(user, receiver);

		friendRequestRepository.save(friendRequest);
	}

	@Transactional
	public void acceptFriendRequest(UserPrincipal userPrincipal, Long requestId) {
		FriendRequest friendRequest = friendRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));

		if(!friendRequest.getReceiver().getEmail().equals(userPrincipal.getEmail())) {
			throw new IllegalArgumentException("친구 요청을 수락할 권한이 없습니다.");
		}

		friendRequest.accept();
		friendRequestRepository.save(friendRequest);

		Friend friend = new Friend(friendRequest.getSender(), friendRequest.getReceiver());
		friendRepository.save(friend);
	}
}
