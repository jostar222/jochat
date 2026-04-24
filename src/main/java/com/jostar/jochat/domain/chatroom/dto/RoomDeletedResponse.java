package com.jostar.jochat.domain.chatroom.dto;

public record RoomDeletedResponse(
        Long roomId,
        String message
) {
}