package com.thales.bcb.modules.message.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class MessageInConversationDTO {
    private UUID id;
    private UUID senderId;
    private UUID recipientId;
    private String content;
    private Instant timestamp;
    private String priority;
    private String status;
}
