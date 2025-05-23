package com.thales.bcb.modules.message.dto;

import com.thales.bcb.modules.message.enums.Priority;
import lombok.Data;

import java.util.UUID;

@Data
public class MessageRequestDTO {
    private UUID conversationId;
    private UUID recipientId;
    private String content;
    private Priority priority;
}
