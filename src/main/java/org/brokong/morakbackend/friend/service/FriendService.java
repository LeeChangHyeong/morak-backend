package org.brokong.morakbackend.friend.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.friend.entity.Friend;
import org.brokong.morakbackend.friend.entity.FriendRequest;
import org.brokong.morakbackend.friend.enums.FriendRequestStatus;
import org.brokong.morakbackend.friend.repository.BlockRepository;
import org.brokong.morakbackend.friend.repository.FriendRepository;
import org.brokong.morakbackend.friend.repository.FriendRequestRepository;
import org.brokong.morakbackend.global.security.UserPrincipal;
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
	private final BlockRepository blockRepository;

	@Transactional
	public void sendFriendRequest(UserPrincipal userPrincipal, Long receiverId) {
		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		// 자기 자신에게 요청 방지
		if (user.getId().equals(receiverId)) {
			throw new IllegalArgumentException("자기 자신에게는 친구 요청을 할 수 없습니다.");
		}

		// 차단 체크 1: 내가 차단한 사용자
		if (blockRepository.existsByBlockerAndBlocked(user, receiver)) {
			throw new IllegalArgumentException("차단한 사용자에게는 친구 요청을 보낼 수 없습니다.");
		}

		// 차단 체크 2: 상대가 나를 차단한 경우
		if (blockRepository.existsByBlockerAndBlocked(receiver, user)) {
			throw new IllegalArgumentException("상대방에게 차단당한 상태입니다.");
		}


		// 중복 요청 방지 - 양방향 모두 확인
		boolean alreadyRequested = friendRequestRepository
				.findBySenderAndReceiverAndFriendRequestStatus(user, receiver, FriendRequestStatus.PENDING).isPresent()
				|| friendRequestRepository.findBySenderAndReceiverAndFriendRequestStatus(receiver, user, FriendRequestStatus.PENDING).isPresent();


		if (alreadyRequested) {
			throw new IllegalArgumentException("이미 친구 요청이 존재합니다.");
		}

		FriendRequest friendRequest = new FriendRequest(user, receiver);

		friendRequestRepository.save(friendRequest);
	}

	@Transactional
	public void acceptFriendRequest(UserPrincipal userPrincipal, Long requestId) {
		FriendRequest friendRequest = friendRequestRepository.findById(requestId)
															 .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));

		if (!friendRequest.getReceiver().getEmail().equals(userPrincipal.getEmail())) {
			throw new IllegalArgumentException("친구 요청을 수락할 권한이 없습니다.");
		}

		friendRequest.accept();
		friendRequestRepository.save(friendRequest);

		Friend friend = new Friend(friendRequest.getSender(), friendRequest.getReceiver());
		friendRepository.save(friend);
	}

	@Transactional
	public void rejectFriendRequest(UserPrincipal userPrincipal, Long requestId) {
		FriendRequest friendRequest = friendRequestRepository.findById(requestId)
															 .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));

		if (!friendRequest.getReceiver().getEmail().equals(userPrincipal.getEmail())) {
			throw new IllegalArgumentException("친구 요청을 거절할 권한이 없습니다.");
		}

		friendRequest.reject();
		friendRequestRepository.save(friendRequest);

		Friend friend = new Friend(friendRequest.getSender(), friendRequest.getReceiver());
		friendRepository.save(friend);
	}

	@Transactional
	public void deleteFriend(UserPrincipal userPrincipal, Long friendId) {
		Friend friend = friendRepository.findById(friendId)
										.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 관계입니다."));

		String currentUserEmail = userPrincipal.getEmail();

		// 현재 유저가 sender 또는 receiver가 아니면 삭제 권한 없음
		boolean isAuthorized = friend.getSender().getEmail().equals(currentUserEmail) ||
							   friend.getReceiver().getEmail().equals(currentUserEmail);

		if (!isAuthorized) {
			throw new IllegalArgumentException("해당 친구 관계를 삭제할 권한이 없습니다.");
		}

		friendRepository.delete(friend);
	}

}
