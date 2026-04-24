package com.jostar.jochat.domain.random.controller;

import com.jostar.jochat.domain.random.dto.RandomMatchRequestResponse;
import com.jostar.jochat.domain.random.service.RandomChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/random-chat")
public class RandomChatApiController {

    private final RandomChatService randomChatService;

    @PostMapping("/match")
    public RandomMatchRequestResponse requestMatch(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean waiting = randomChatService.requestMatch(userDetails.getUsername());

        if (waiting) {
            return new RandomMatchRequestResponse(
                    true,
                    true,
                    "랜덤채팅 상대를 기다리는 중입니다."
            );
        }

        return new RandomMatchRequestResponse(
                true,
                false,
                "랜덤채팅이 매칭되었습니다."
        );
    }

    @PostMapping("/cancel")
    public RandomMatchRequestResponse cancel(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        randomChatService.cancelWaiting(userDetails.getUsername());

        return new RandomMatchRequestResponse(
                true,
                false,
                "랜덤채팅 대기를 취소했습니다."
        );
    }

    @GetMapping("/online-count")
    public int onlineCount() {
        return randomChatService.getOnlineCount();
    }
}