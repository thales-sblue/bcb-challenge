package com.thales.bcb.modules.conversation.mapper;

import com.thales.bcb.modules.conversation.dto.ConversationResponseDTO;
import com.thales.bcb.modules.conversation.entity.Conversation;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConversationMapper {

    public Conversation toEntity(UUID clientId, UUID recipientId, String recipientName){
        return Conversation.builder()
                .clientId(clientId)
                .recipientId(recipientId)
                .recipientName(recipientName)
                .lastMessageContent(null)
                .lastMessageTime(null)
                .unreadCount(0)
                .build();
    }
    public ConversationResponseDTO toResponse(Conversation conversation){
        return ConversationResponseDTO.builder()
                .id(conversation.getId())
                .clientId(conversation.getClientId())
                .recipientId(conversation.getRecipientId())
                .recipientName(conversation.getRecipientName())
                .lastMessageContent(conversation.getLastMessageContent())
                .lastMessageTime(conversation.getLastMessageTime())
                .unreadCount(conversation.getUnreadCount())
                .build();
    }
}
