package com.jostar.jochat.domain.message.controller;

import com.jostar.jochat.domain.message.dto.ChatMessageResponse;
import com.jostar.jochat.domain.message.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatMessageApiController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{roomId}/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long roomId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        Long loginUserId = chatMessageService.getLoginUserIdByUsername(userDetails.getUsername());
        return chatMessageService.getRecentMessages(roomId, loginUserId);
    }
}