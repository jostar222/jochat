package com.jostar.jochat.domain.message.controller;

import com.jostar.jochat.domain.message.dto.ChatMessageResponse;
import com.jostar.jochat.domain.message.dto.ChatMessageSocketRequest;
import com.jostar.jochat.domain.message.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageWsController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void send(ChatMessageSocketRequest request, Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("WebSocket 인증 정보가 없습니다.");
        }

        String username = principal.getName();
        Long senderId = chatMessageService.getLoginUserIdByUsername(username);

        ChatMessageResponse response = chatMessageService.saveMessage(
                request.roomId(),
                senderId,
                request.content()
        );

        messagingTemplate.convertAndSend(
                "/sub/chat.room." + request.roomId(),
                response
        );
    }
}