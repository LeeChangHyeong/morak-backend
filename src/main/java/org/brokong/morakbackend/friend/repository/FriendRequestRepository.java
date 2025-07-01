package org.brokong.morakbackend.friend.repository;

import java.util.List;
import java.util.Optional;
import org.brokong.morakbackend.friend.entity.FriendRequest;
import org.brokong.morakbackend.friend.enums.FriendRequestStatus;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

	Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);

	Optional<FriendRequest> findBySenderAndReceiverAndFriendRequestStatus(User sender, User receiver, FriendRequestStatus status);

	/**
	 * 받은 친구 요청 목록 조회
	 */
	List<FriendRequest> findByReceiverAndFriendRequestStatus(User receiver, FriendRequestStatus status);

	/**
	 * 보낸 친구 요청 목록 조회
	 */
	List<FriendRequest> findBySenderAndFriendRequestStatus(User sender, FriendRequestStatus status);
}
