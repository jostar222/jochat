package com.jostar.jochat.domain.chatroom.dto;

import java.time.LocalDateTime;

public record ChatRoomListItemResponse(
        Long roomId,
        String roomName,
        String lastMessage,
        LocalDateTime lastMessageTime,
        long unreadCount
) {
}