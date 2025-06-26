package org.brokong.morakbackend.friend.repository;

import java.util.Optional;
import org.brokong.morakbackend.friend.entity.Friend;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
	Optional<Friend> findBySenderAndReceiver(User user1, User user2);
}