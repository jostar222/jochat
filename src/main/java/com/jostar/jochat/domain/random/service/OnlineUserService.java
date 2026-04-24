package com.jostar.jochat.domain.random.service;

import com.jostar.jochat.domain.random.dto.OnlineUserCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OnlineUserService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, Set<String>> onlineUsers = new ConcurrentHashMap<>();

    public void connect(String username, String sessionId) {
        onlineUsers
                .computeIfAbsent(username, key -> ConcurrentHashMap.newKeySet())
                .add(sessionId);

        broadcastOnlineCount();
    }

    public void disconnect(String username, String sessionId) {
        Set<String> sessions = onlineUsers.get(username);

        if (sessions != null) {
            sessions.remove(sessionId);

            if (sessions.isEmpty()) {
                onlineUsers.remove(username);
            }
        }

        broadcastOnlineCount();
    }

    public int getOnlineCount() {
        return onlineUsers.size();
    }

    public List<String> getOtherOnlineUsernames(String myUsername) {
        return onlineUsers.keySet()
                .stream()
                .filter(username -> !username.equals(myUsername))
                .toList();
    }

    private void broadcastOnlineCount() {
        messagingTemplate.convertAndSend(
                "/sub/online.count",
                new OnlineUserCountResponse(getOnlineCount())
        );
    }
}