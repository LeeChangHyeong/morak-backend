package org.brokong.morakbackend.friend.repository;

import java.util.Optional;
import org.brokong.morakbackend.friend.entity.Friend;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

	@Query("SELECT f FROM Friend f " +
		   "WHERE (f.sender = :user1 AND f.receiver = :user2) " +
		   "   OR (f.sender = :user2 AND f.receiver = :user1)")
	Optional<Friend> findByUsers(User user1, User user2);
}