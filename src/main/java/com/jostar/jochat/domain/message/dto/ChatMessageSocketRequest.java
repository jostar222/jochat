package com.jostar.jochat.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChatMessageSocketRequest(
        @NotNull Long roomId,
        @NotBlank @Size(max = 1000) String content
) {
}