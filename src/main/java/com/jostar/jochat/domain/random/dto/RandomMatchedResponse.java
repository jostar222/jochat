package com.jostar.jochat.domain.random.dto;

public record RandomMatchedResponse(
        boolean matched,
        Long roomId,
        String message
) {
}