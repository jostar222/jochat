package com.jostar.jochat.domain.chatroom.dto;

public record RoomListUpdateResponse(
        Long roomId,
        String lastMessage,
        String lastMessageTime,
        long unreadCount
) {
}