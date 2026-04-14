package com.jostar.jochat.domain.chatroom.controller;

import com.jostar.jochat.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/{roomId}/read")
    public Map<String, Object> markAsRead(@PathVariable Long roomId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        Long loginUserId = chatRoomService.getLoginUserIdByUsername(userDetails.getUsername());

        chatRoomService.validateRoomMember(roomId, loginUserId);
        chatRoomService.markAsRead(roomId, loginUserId);

        return Map.of(
                "success", true,
                "roomId", roomId
        );
    }
}