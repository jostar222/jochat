package com.jostar.jochat.domain.message.repository;

import com.jostar.jochat.domain.message.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop50ByChatRoomIdOrderByCreatedAtDesc(Long roomId);

    Optional<ChatMessage> findTopByChatRoomIdOrderByCreatedAtDesc(Long roomId);

    long countByChatRoomIdAndIdGreaterThanAndSenderIdNot(Long roomId, Long messageId, Long senderId);
}