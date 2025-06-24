package org.brokong.morakbackend.friend.repository;

import java.util.Optional;
import org.brokong.morakbackend.friend.entity.FriendRequest;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

	Optional<Object> findBySenderAndReceiver(User user, User receiver);
}
