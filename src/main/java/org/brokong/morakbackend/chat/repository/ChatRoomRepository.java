package org.brokong.morakbackend.chat.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.brokong.morakbackend.chat.entity.ChatRoom;
import org.brokong.morakbackend.chat.enums.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	@Query("""
    select cr from ChatRoom cr
    join fetch cr.members m
    join cr.members m1
    join cr.members m2
    where m1.user.id = :userId1 and m2.user.id = :userId2
    and cr.type = :type
""")
	Optional<ChatRoom> findDirectChatRoomByUserIds(@Param("userId1") Long userId1,
												   @Param("userId2") Long userId2,
												   @Param("type") ChatRoomType type);

}
