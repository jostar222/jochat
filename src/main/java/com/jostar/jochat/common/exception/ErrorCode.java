package com.jostar.jochat.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 사용 중인 아이디입니다."),
    SELF_CHAT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신과는 채팅할 수 없습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    NOT_CHAT_ROOM_MEMBER(HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다."),
    INVALID_MESSAGE(HttpStatus.BAD_REQUEST, "유효하지 않은 메시지입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}