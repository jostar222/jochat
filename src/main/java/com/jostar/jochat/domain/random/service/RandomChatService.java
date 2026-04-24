package com.jostar.jochat.domain.random.service;

import com.jostar.jochat.domain.chatroom.service.ChatRoomService;
import com.jostar.jochat.domain.random.dto.RandomMatchedResponse;
import com.jostar.jochat.domain.user.entity.User;
import com.jostar.jochat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RandomChatService {

    private final OnlineUserService onlineUserService;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Set<String> waitingUsers = ConcurrentHashMap.newKeySet();

    @Transactional
    public boolean requestMatch(String username) {
        if (waitingUsers.contains(username)) {
            return true;
        }

        List<String> onlineCandidates = onlineUserService.getOtherOnlineUsernames(username);

        List<String> candidates = waitingUsers.stream()
                .filter(waitingUsername -> !waitingUsername.equals(username))
                .filter(onlineCandidates::contains)
                .toList();

        if (candidates.isEmpty()) {
            waitingUsers.add(username);
            return true;
        }

        String targetUsername = candidates.get(
                ThreadLocalRandom.current().nextInt(candidates.size())
        );

        waitingUsers.remove(username);
        waitingUsers.remove(targetUsername);

        User me = userRepository.findByUsername(username)
                .orElseThrow();

        User target = userRepository.findByUsername(targetUsername)
                .orElseThrow();

        Long roomId = chatRoomService.createOrGetDirectRoom(
                me.getId(),
                target.getId()
        );

        sendMatched(username, roomId);
        sendMatched(targetUsername, roomId);

        return false;
    }

    public void cancelWaiting(String username) {
        waitingUsers.remove(username);
    }

    public int getOnlineCount() {
        return onlineUserService.getOnlineCount();
    }

    private void sendMatched(String username, Long roomId) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/random-match",
                new RandomMatchedResponse(
                        true,
                        roomId,
                        "랜덤채팅이 매칭되었습니다."
                )
        );
    }
}