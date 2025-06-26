package org.brokong.morakbackend.friend.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.friend.entity.Block;
import org.brokong.morakbackend.friend.repository.BlockRepository;
import org.brokong.morakbackend.friend.repository.FriendRepository;
import org.brokong.morakbackend.global.Security.UserPrincipal;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockService {

	private final UserRepository userRepository;
	private final FriendRepository friendRepository;
	private final BlockRepository blockRepository;

	@Transactional
	public void blockUser(UserPrincipal userPrincipal, Long blockedUserId) {
		User blocker = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		User blockedUser = userRepository.findById(blockedUserId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		if (blocker.equals(blockedUser)) {
			throw new IllegalArgumentException("자기 자신을 차단할 수 없습니다.");
		}

		if (blockRepository.existsByBlockerAndBlocked(blocker, blockedUser)) {
			throw new IllegalArgumentException("이미 차단한 사용자입니다.");
		}

		// 기존 친구 관계가 있다면 삭제 (양방향 확인)
		friendRepository.findByUsers(blocker, blockedUser)
						.ifPresent(friendRepository::delete);

		blockRepository.save(new Block(blocker, blockedUser));
	}

	@Transactional
	public void unblockUser(UserPrincipal userPrincipal, Long blockedUserId) {
		User blocker = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		User blockedUser = userRepository.findById(blockedUserId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		Block block = blockRepository.findByBlockerAndBlocked(blocker, blockedUser)
									 .orElseThrow(() -> new IllegalArgumentException("차단하지 않은 사용자입니다."));

		blockRepository.delete(block);
	}
}
