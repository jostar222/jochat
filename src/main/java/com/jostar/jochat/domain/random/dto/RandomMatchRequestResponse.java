package com.jostar.jochat.domain.random.dto;

public record RandomMatchRequestResponse(
        boolean success,
        boolean waiting,
        String message
) {
}