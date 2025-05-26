package com.thales.bcb.modules.conversation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private UUID recipientId;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String lastMessageContent;

    @Column(nullable = false)
    private Instant lastMessageTime;

    private Integer unreadCount;

}
