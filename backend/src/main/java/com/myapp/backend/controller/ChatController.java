package com.myapp.backend.controller;

import com.myapp.backend.model.dto.ChatMessageDto;
import com.myapp.backend.model.dto.ChatUserDto;
import com.myapp.backend.service.ChatAuditService;
import com.myapp.backend.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatAuditService chatAuditService;

    public ChatController(ChatService chatService, ChatAuditService chatAuditService) {
        this.chatService = chatService;
        this.chatAuditService = chatAuditService;
    }

    @PostMapping("/register")
    public ChatUserDto register(@RequestBody Map<String, String> body) {
        String userName = body.getOrDefault("userName", "Anonymous");
        return chatService.register(userName);
    }

    @PostMapping("/unregister")
    public ResponseEntity<Void> unregister(@RequestBody Map<String, String> body) {
        chatService.unregister(body.get("userId"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages")
    public List<ChatMessageDto> getRecentMessages(@RequestParam(value = "count", defaultValue = "50") int count) {
        return chatService.getRecentMessages(count);
    }

    @GetMapping("/users")
    public List<ChatUserDto> getOnlineUsers() {
        return chatService.getOnlineUsers();
    }

    @MessageMapping("/chat.send")
    public void handleSendMessage(@Payload Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        String text = (String) payload.get("text");
        @SuppressWarnings("unchecked")
        List<String> mediaUrls = (List<String>) payload.get("mediaUrls");
        chatService.sendMessage(userId, text, mediaUrls);
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, String> payload) {
        chatService.typing(payload.get("userId"));
    }
}
