package com.myapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "chat_message_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String userName;

    @Column(length = 10)
    private String messageType;

    @Column(columnDefinition = "TEXT")
    private String messageText;

    @Column(columnDefinition = "TEXT")
    private String mediaUrls;

    @Column(length = 50)
    private String senderIp;

    @Column(length = 500)
    private String userAgent;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant sentAt;
}
