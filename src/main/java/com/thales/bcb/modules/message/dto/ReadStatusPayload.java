package com.thales.bcb.modules.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadStatusPayload{
    private UUID conversationId;
    private UUID readerId;
    private List<UUID> messageIds;
}
