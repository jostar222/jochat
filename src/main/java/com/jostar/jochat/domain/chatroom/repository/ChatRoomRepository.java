package com.jostar.jochat.domain.chatroom.repository;

import com.jostar.jochat.domain.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}