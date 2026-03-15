package com.myapp.backend.model.dto;

import java.time.Instant;
import java.util.List;

public record ChatMessageDto(
    String userId,
    String userName,
    String type,
    String text,
    List<String> mediaUrls,
    Instant timestamp
) {}
