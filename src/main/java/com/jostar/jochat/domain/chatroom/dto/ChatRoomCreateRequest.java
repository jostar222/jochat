package com.jostar.jochat.domain.chatroom.dto;

import jakarta.validation.constraints.NotNull;

public record ChatRoomCreateRequest(
        @NotNull Long targetUserId
) {
}