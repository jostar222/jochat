package com.jostar.jochat.domain.chatroom.repository;

import com.jostar.jochat.domain.chatroom.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user"})
    List<ChatRoomMember> findByChatRoomId(Long roomId);

    boolean existsByChatRoomIdAndUserId(Long roomId, Long userId);

    Optional<ChatRoomMember> findByChatRoomIdAndUserId(Long roomId, Long userId);

    void deleteByChatRoomId(Long roomId);

    long countByChatRoomId(Long roomId);
}