package com.thales.bcb.modules.conversation.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ConversationSummaryDTO {
    private UUID id;
    private UUID clientId;
    private UUID recipientId;
    private String recipientName;
    private String lastMessageContent;
    private Instant lastMessageTime;
    private Integer unreadCount;
}

