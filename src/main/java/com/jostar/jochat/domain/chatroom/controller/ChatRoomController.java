package com.jostar.jochat.domain.chatroom.controller;

import com.jostar.jochat.domain.chatroom.dto.ChatRoomCreateRequest;
import com.jostar.jochat.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/rooms")
    public String rooms(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long loginUserId = chatRoomService.getLoginUserIdByUsername(userDetails.getUsername());

        model.addAttribute("rooms", chatRoomService.getMyRooms(loginUserId));
        model.addAttribute("users", chatRoomService.getOtherUsers(loginUserId));

        return "chat/rooms";
    }

    @PostMapping("/rooms/direct")
    public String createDirectRoom(@AuthenticationPrincipal UserDetails userDetails,
                                   @ModelAttribute ChatRoomCreateRequest request) {
        Long loginUserId = chatRoomService.getLoginUserIdByUsername(userDetails.getUsername());
        Long roomId = chatRoomService.createOrGetDirectRoom(loginUserId, request.targetUserId());

        return "redirect:/chat/rooms/" + roomId;
    }

    @GetMapping("/rooms/{roomId}")
    public String room(@PathVariable Long roomId,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        Long loginUserId = chatRoomService.getLoginUserIdByUsername(userDetails.getUsername());

        chatRoomService.validateRoomMember(roomId, loginUserId);

        model.addAttribute("room", chatRoomService.getRoom(roomId));
        model.addAttribute("loginUserId", loginUserId);

        return "chat/room";
    }
}