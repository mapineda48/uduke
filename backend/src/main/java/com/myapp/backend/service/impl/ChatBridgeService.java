package com.myapp.backend.service.impl;

import com.myapp.backend.service.PubSubService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatBridgeService {

    private final PubSubService pubSubService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatBridgeService(PubSubService pubSubService, SimpMessagingTemplate messagingTemplate) {
        this.pubSubService = pubSubService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        pubSubService.subscribe(ChatServiceImpl.CHANNEL_MESSAGE, message ->
            messagingTemplate.convertAndSend("/topic/chat.message", message));

        pubSubService.subscribe(ChatServiceImpl.CHANNEL_USER_JOINED, message ->
            messagingTemplate.convertAndSend("/topic/chat.user-joined", message));

        pubSubService.subscribe(ChatServiceImpl.CHANNEL_USER_LEFT, message ->
            messagingTemplate.convertAndSend("/topic/chat.user-left", message));

        pubSubService.subscribe(ChatServiceImpl.CHANNEL_TYPING, message ->
            messagingTemplate.convertAndSend("/topic/chat.typing", message));
    }

    @PreDestroy
    public void cleanup() {
        pubSubService.unsubscribeAll();
    }
}
