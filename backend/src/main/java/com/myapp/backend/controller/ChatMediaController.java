package com.myapp.backend.controller;

import com.myapp.backend.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatMediaController {

    private static final String CONTAINER = "chat-media";

    private final StorageService storageService;

    public ChatMediaController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadMedia(@RequestPart("file") MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
            ? originalName.substring(originalName.lastIndexOf('.'))
            : "";
        String blobName = UUID.randomUUID() + ext;
        String url = storageService.upload(CONTAINER, blobName,
            file.getInputStream(), file.getSize(), file.getContentType());
        return ResponseEntity.ok(Map.of("url", url));
    }
}
