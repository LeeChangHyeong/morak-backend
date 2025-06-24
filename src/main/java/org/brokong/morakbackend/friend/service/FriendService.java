package org.brokong.morakbackend.friend.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.friend.entity.FriendRequest;
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

	@Transactional
	public void sendFriendRequest(UserPrincipal userPrincipal, Long receiverId) {
		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		// 자기 자신에게 요청 방지
		if (user.getId().equals(receiverId)) {
			throw new IllegalArgumentException("자기 자신에게는 친구 요청을 할 수 없습니다.");
		}

		// 중복 요청 방지
		friendRequestRepository.findBySenderAndReceiver(user, receiver).ifPresent(friendRequest -> {
			throw new IllegalArgumentException("이미 친구 요청을 보냈습니다.");
		});

		FriendRequest friendRequest = new FriendRequest(user, receiver);

		friendRequestRepository.save(friendRequest);
	}
}
