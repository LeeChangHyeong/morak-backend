package org.brokong.morakbackend.chat.repository;

import java.util.List;
import org.brokong.morakbackend.chat.entity.ChatRoom;
import org.brokong.morakbackend.chat.enums.ChatRoomType;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	List<ChatRoom> findByType(ChatRoomType type);
	List<ChatRoom> findByCreaterAndType(User creater, ChatRoomType type);
	List<ChatRoom> findByNameContainingAndType(String name, ChatRoomType type);

}
