package com.jostar.jochat.domain.chatroom.service;

import com.jostar.jochat.common.exception.BusinessException;
import com.jostar.jochat.common.exception.ErrorCode;
import com.jostar.jochat.domain.chatroom.dto.ChatRoomListItemResponse;
import com.jostar.jochat.domain.chatroom.entity.ChatRoom;
import com.jostar.jochat.domain.chatroom.entity.ChatRoomMember;
import com.jostar.jochat.domain.chatroom.entity.RoomType;
import com.jostar.jochat.domain.chatroom.repository.ChatRoomMemberRepository;
import com.jostar.jochat.domain.chatroom.repository.ChatRoomRepository;
import com.jostar.jochat.domain.message.entity.ChatMessage;
import com.jostar.jochat.domain.message.repository.ChatMessageRepository;
import com.jostar.jochat.domain.user.entity.User;
import com.jostar.jochat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomListItemResponse> getMyRooms(Long loginUserId) {
        List<ChatRoomMember> members = chatRoomMemberRepository.findByUserId(loginUserId);

        return members.stream()
                .map(ChatRoomMember::getChatRoom)
                .map(room -> {
                    Optional<ChatMessage> lastMessage = chatMessageRepository
                            .findTopByChatRoomIdOrderByCreatedAtDesc(room.getId());

                    return new ChatRoomListItemResponse(
                            room.getId(),
                            room.getRoomName(),
                            lastMessage.map(ChatMessage::getContent).orElse(""),
                            lastMessage.map(ChatMessage::getCreatedAt).orElse(null)
                    );
                })
                .sorted(Comparator.comparing(
                        ChatRoomListItemResponse::lastMessageTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    @Transactional
    public Long createOrGetDirectRoom(Long loginUserId, Long targetUserId) {
        if (Objects.equals(loginUserId, targetUserId)) {
            throw new BusinessException(ErrorCode.SELF_CHAT_NOT_ALLOWED);
        }

        User loginUser = userRepository.findById(loginUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<ChatRoomMember> myMemberships = chatRoomMemberRepository.findByUserId(loginUserId);

        for (ChatRoomMember membership : myMemberships) {
            Long roomId = membership.getChatRoom().getId();
            List<ChatRoomMember> roomMembers = chatRoomMemberRepository.findByChatRoomId(roomId);

            if (roomMembers.size() == 2) {
                Set<Long> userIds = roomMembers.stream()
                        .map(member -> member.getUser().getId())
                        .collect(Collectors.toSet());

                if (userIds.contains(loginUserId) && userIds.contains(targetUserId)) {
                    return roomId;
                }
            }
        }

        ChatRoom room = chatRoomRepository.save(
                ChatRoom.builder()
                        .roomName(targetUser.getNickname())
                        .roomType(RoomType.DIRECT)
                        .build()
        );

        chatRoomMemberRepository.save(
                ChatRoomMember.builder()
                        .chatRoom(room)
                        .user(loginUser)
                        .build()
        );

        chatRoomMemberRepository.save(
                ChatRoomMember.builder()
                        .chatRoom(room)
                        .user(targetUser)
                        .build()
        );

        return room.getId();
    }

    @Transactional(readOnly = true)
    public void validateRoomMember(Long roomId, Long userId) {
        if (!chatRoomMemberRepository.existsByChatRoomIdAndUserId(roomId, userId)) {
            throw new BusinessException(ErrorCode.NOT_CHAT_ROOM_MEMBER);
        }
    }

    @Transactional(readOnly = true)
    public ChatRoom getRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<User> getOtherUsers(Long loginUserId) {
        return userRepository.findByIdNot(loginUserId);
    }

    @Transactional(readOnly = true)
    public Long getLoginUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getId();
    }
}