package com.jostar.jochat.domain.chatroom.repository;

import com.jostar.jochat.domain.chatroom.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByUserId(Long userId);

    List<ChatRoomMember> findByChatRoomId(Long roomId);

    boolean existsByChatRoomIdAndUserId(Long roomId, Long userId);
}