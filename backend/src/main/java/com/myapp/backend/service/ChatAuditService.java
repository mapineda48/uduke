package com.myapp.backend.service;

import com.myapp.backend.model.dto.ChatMessageDto;

public interface ChatAuditService {

    void logMessage(ChatMessageDto message, String senderIp, String userAgent);
}
