package org.brokong.morakbackend.friend.repository;

import java.util.Optional;
import org.brokong.morakbackend.friend.entity.Block;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

	boolean existsByBlockerAndBlocked(User blocker, User blockedUser);

	Optional<Block> findByBlockerAndBlocked(User blocker, User blockedUser);
}
