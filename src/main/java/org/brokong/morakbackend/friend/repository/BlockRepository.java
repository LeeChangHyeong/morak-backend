package org.brokong.morakbackend.friend.repository;

import java.util.List;
import java.util.Optional;
import org.brokong.morakbackend.friend.entity.Block;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

	boolean existsByBlockerAndBlocked(User blocker, User blockedUser);

	Optional<Block> findByBlockerAndBlocked(User blocker, User blockedUser);

	// 내가 차단한 사용자들의 ID 목록 조회
	@Query("SELECT b.blocked.id FROM Block b WHERE b.blocker.id = :blockerId")
	List<Long> findBlockedUserIdsByBlockerId(@Param("blockerId") Long blockerId);
}
