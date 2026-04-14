package com.jostar.jochat.domain.message.service;

import com.jostar.jochat.common.exception.BusinessException;
import com.jostar.jochat.common.exception.ErrorCode;
import com.jostar.jochat.domain.chatroom.entity.ChatRoom;
import com.jostar.jochat.domain.chatroom.service.ChatRoomService;
import com.jostar.jochat.domain.message.dto.ChatMessageResponse;
import com.jostar.jochat.domain.message.entity.ChatMessage;
import com.jostar.jochat.domain.message.repository.ChatMessageRepository;
import com.jostar.jochat.domain.user.entity.User;
import com.jostar.jochat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, Long senderId, String content) {
        if (content == null || content.isBlank() || content.length() > 1000) {
            throw new BusinessException(ErrorCode.INVALID_MESSAGE);
        }

        chatRoomService.validateRoomMember(roomId, senderId);

        ChatRoom room = chatRoomService.getRoom(roomId);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ChatMessage message = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(room)
                        .sender(sender)
                        .content(content.trim())
                        .build()
        );

        return new ChatMessageResponse(
                message.getId(),
                roomId,
                sender.getId(),
                sender.getNickname(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getRecentMessages(Long roomId, Long loginUserId) {
        chatRoomService.validateRoomMember(roomId, loginUserId);

        return chatMessageRepository.findTop50ByChatRoomIdOrderByCreatedAtDesc(roomId)
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .map(message -> new ChatMessageResponse(
                        message.getId(),
                        message.getChatRoom().getId(),
                        message.getSender().getId(),
                        message.getSender().getNickname(),
                        message.getContent(),
                        message.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Long getLoginUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getId();
    }
}