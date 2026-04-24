package com.jostar.jochat.domain.message.controller;

import com.jostar.jochat.domain.chatroom.dto.RoomListUpdateResponse;
import com.jostar.jochat.domain.chatroom.entity.ChatRoomMember;
import com.jostar.jochat.domain.chatroom.repository.ChatRoomMemberRepository;
import com.jostar.jochat.domain.chatroom.service.ChatRoomService;
import com.jostar.jochat.domain.message.dto.ChatMessageResponse;
import com.jostar.jochat.domain.message.dto.ChatMessageSocketRequest;
import com.jostar.jochat.domain.message.service.ChatMessageService;
import com.jostar.jochat.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatMessageWsController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
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

        // 1) 채팅방 메시지 전송
        messagingTemplate.convertAndSend(
                "/sub/chat.room." + request.roomId(),
                response
        );

        // 2) 방 참여자 각각에게 목록 갱신 이벤트 전송
        List<ChatRoomMember> roomMembers = chatRoomMemberRepository.findByChatRoomId(request.roomId());

        for (ChatRoomMember member : roomMembers) {
            User user = member.getUser();

            RoomListUpdateResponse roomListUpdate = chatRoomService.buildRoomListUpdate(
                    request.roomId(),
                    user.getId()
            );

            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),
                    "/queue/rooms",
                    roomListUpdate
            );
        }
    }
}