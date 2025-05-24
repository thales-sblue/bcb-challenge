package com.thales.bcb.modules.conversation.mapper;

import com.thales.bcb.modules.conversation.dto.ConversationResponseDTO;
import com.thales.bcb.modules.conversation.dto.ConversationSummaryDTO;
import com.thales.bcb.modules.conversation.entity.Conversation;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConversationMapper {

    private final MessageMapper messageMapper;

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
    public ConversationResponseDTO toResponse(Conversation conversation, List<Message> messages){
        return ConversationResponseDTO.builder()
                .id(conversation.getId())
                .clientId(conversation.getClientId())
                .recipientId(conversation.getRecipientId())
                .recipientName(conversation.getRecipientName())
                .lastMessageContent(conversation.getLastMessageContent())
                .lastMessageTime(conversation.getLastMessageTime())
                .unreadCount(conversation.getUnreadCount())
                .messages(messages.stream()
                        .map(messageMapper::toInConversationDTO)
                        .toList())
                .build();
    }

    public ConversationSummaryDTO toSummary(Conversation conversation){
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
