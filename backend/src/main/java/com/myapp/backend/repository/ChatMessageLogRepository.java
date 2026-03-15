package com.myapp.backend.repository;

import com.myapp.backend.model.entity.ChatMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageLogRepository extends JpaRepository<ChatMessageLog, Long> {
}
