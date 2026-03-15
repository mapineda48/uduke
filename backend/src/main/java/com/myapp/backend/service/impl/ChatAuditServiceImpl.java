package com.myapp.backend.service.impl;

import com.myapp.backend.model.dto.ChatMessageDto;
import com.myapp.backend.model.entity.ChatMessageLog;
import com.myapp.backend.repository.ChatMessageLogRepository;
import com.myapp.backend.service.ChatAuditService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ChatAuditServiceImpl implements ChatAuditService {

    private final ChatMessageLogRepository repository;

    public ChatAuditServiceImpl(ChatMessageLogRepository repository) {
        this.repository = repository;
    }

    @Async
    @Override
    public void logMessage(ChatMessageDto message, String senderIp, String userAgent) {
        ChatMessageLog log = ChatMessageLog.builder()
            .userName(message.userName())
            .messageType(message.type())
            .messageText(message.text())
            .mediaUrls(message.mediaUrls() != null ? String.join(",", message.mediaUrls()) : null)
            .senderIp(senderIp)
            .userAgent(userAgent)
            .build();
        repository.save(log);
    }
}
