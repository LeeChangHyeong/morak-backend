package org.brokong.morakbackend.friend.repository;

import java.util.List;
import java.util.Optional;
import org.brokong.morakbackend.friend.entity.Friend;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

	@Query("SELECT f FROM Friend f " +
		   "WHERE (f.sender = :user1 AND f.receiver = :user2) " +
		   "   OR (f.sender = :user2 AND f.receiver = :user1)")
	Optional<Friend> findByUsers(User user1, User user2);

	/**
	 * 특정 사용자의 모든 친구 관계 조회
	 */
	@Query("SELECT f FROM Friend f " +
		   "WHERE f.sender = :user OR f.receiver = :user")
	List<Friend> findByUsers(@Param("user") User user);

	/**
	 * 두 사용자가 친구인지 확인
	 */
	@Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friend f " +
		   "WHERE (f.sender = :user1 AND f.receiver = :user2) " +
		   "   OR (f.sender = :user2 AND f.receiver = :user1)")
	boolean existsByUsers(@Param("user1") User user1, @Param("user2") User user2);
}