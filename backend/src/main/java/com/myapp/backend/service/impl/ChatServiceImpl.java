package com.myapp.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.backend.model.dto.ChatMessageDto;
import com.myapp.backend.model.dto.ChatUserDto;
import com.myapp.backend.service.CacheService;
import com.myapp.backend.service.ChatService;
import com.myapp.backend.service.PubSubService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class ChatServiceImpl implements ChatService {

    private static final String USERS_KEY = "chat:users";
    private static final String MESSAGES_KEY = "chat:messages";
    private static final String TYPING_PREFIX = "chat:typing:";
    private static final int MAX_MESSAGES = 100;

    public static final String CHANNEL_MESSAGE = "chat:events:message";
    public static final String CHANNEL_USER_JOINED = "chat:events:user-joined";
    public static final String CHANNEL_USER_LEFT = "chat:events:user-left";
    public static final String CHANNEL_TYPING = "chat:events:typing";

    private final CacheService cacheService;
    private final PubSubService pubSubService;
    private final ObjectMapper objectMapper;

    public ChatServiceImpl(CacheService cacheService, PubSubService pubSubService, ObjectMapper objectMapper) {
        this.cacheService = cacheService;
        this.pubSubService = pubSubService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChatUserDto register(String userName) {
        String userId = UUID.randomUUID().toString().substring(0, 8);
        ChatUserDto user = new ChatUserDto(userId, userName);
        cacheService.hashPut(USERS_KEY, userId, toJson(user));
        pubSubService.publish(CHANNEL_USER_JOINED, toJson(user));
        return user;
    }

    @Override
    public void unregister(String userId) {
        String userJson = cacheService.hashGet(USERS_KEY, userId);
        if (userJson != null) {
            cacheService.hashDelete(USERS_KEY, userId);
            cacheService.delete(TYPING_PREFIX + userId);
            pubSubService.publish(CHANNEL_USER_LEFT, userJson);
        }
    }

    @Override
    public void sendMessage(String userId, String text, List<String> mediaUrls) {
        String userJson = cacheService.hashGet(USERS_KEY, userId);
        if (userJson == null) {
            throw new IllegalArgumentException("User not registered: " + userId);
        }
        ChatUserDto user = fromJson(userJson, ChatUserDto.class);
        ChatMessageDto message = new ChatMessageDto(
            userId, user.userName(), "message", text,
            mediaUrls != null ? mediaUrls : List.of(),
            Instant.now()
        );
        String messageJson = toJson(message);
        cacheService.listRightPush(MESSAGES_KEY, messageJson);
        cacheService.listTrim(MESSAGES_KEY, -MAX_MESSAGES, -1);
        pubSubService.publish(CHANNEL_MESSAGE, messageJson);
    }

    @Override
    public void sendSystemMessage(String text) {
        ChatMessageDto message = new ChatMessageDto(
            "system", "System", "system", text, List.of(), Instant.now()
        );
        String messageJson = toJson(message);
        cacheService.listRightPush(MESSAGES_KEY, messageJson);
        pubSubService.publish(CHANNEL_MESSAGE, messageJson);
    }

    @Override
    public void typing(String userId) {
        String userJson = cacheService.hashGet(USERS_KEY, userId);
        if (userJson != null) {
            ChatUserDto user = fromJson(userJson, ChatUserDto.class);
            pubSubService.publish(CHANNEL_TYPING, toJson(user));
        }
    }

    @Override
    public List<ChatMessageDto> getRecentMessages(int count) {
        List<String> raw = cacheService.listRange(MESSAGES_KEY, -count, -1);
        if (raw == null) return List.of();
        return raw.stream()
            .map(json -> fromJson(json, ChatMessageDto.class))
            .toList();
    }

    @Override
    public List<ChatUserDto> getOnlineUsers() {
        Map<String, String> all = cacheService.hashGetAll(USERS_KEY);
        return all.values().stream()
            .map(json -> fromJson(json, ChatUserDto.class))
            .toList();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize to JSON", e);
        }
    }

    private <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON", e);
        }
    }
}
