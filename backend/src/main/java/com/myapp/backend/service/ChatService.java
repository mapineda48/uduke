package com.myapp.backend.service;

import com.myapp.backend.model.dto.ChatMessageDto;
import com.myapp.backend.model.dto.ChatUserDto;

import java.util.List;

public interface ChatService {

    ChatUserDto register(String userName);

    void unregister(String userId);

    void sendMessage(String userId, String text, List<String> mediaUrls);

    void sendSystemMessage(String text);

    void typing(String userId);

    List<ChatMessageDto> getRecentMessages(int count);

    List<ChatUserDto> getOnlineUsers();
}
