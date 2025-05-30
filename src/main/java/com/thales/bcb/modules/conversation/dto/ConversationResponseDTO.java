package com.thales.bcb.modules.conversation.dto;

import com.thales.bcb.modules.message.dto.MessageInConversationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponseDTO {
    private UUID id;
    private UUID clientId;
    private UUID recipientId;
    private String recipientName;
    private String lastMessageContent;
    private Instant lastMessageTime;
    private Integer unreadCount;
    private List<MessageInConversationDTO> messages;
}
