package com.thales.bcb.modules.message.mapper;

import com.thales.bcb.modules.message.dto.*;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.enums.Status;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class MessageMapper {

    public Message toEntity (MessageRequestDTO request, UUID senderId, BigDecimal cost){
        return Message.builder()
                .conversationId(request.getConversationId())
                .senderId(senderId)
                .recipientId(request.getRecipientId())
                .content(request.getContent())
                .timestamp(Instant.now())
                .priority(request.getPriority())
                .status(Status.QUEUED)
                .cost(cost)
                .build();
    }

    public MessageResponseDTO toResponse(Message message, BigDecimal currentBalance){
        return MessageResponseDTO.builder()
                .id(message.getId())
                .status(message.getStatus().name())
                .estimatedDelivery(message.getTimestamp().plusSeconds(5))
                .cost(message.getCost())
                .currentBalance(currentBalance)
                .build();
    }

    public MessageInConversationDTO toInConversationDTO(Message message){
        return MessageInConversationDTO.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .priority(message.getPriority().name())
                .status(message.getStatus().name())
                .build();
    }

    public MessageDTO toDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .priority(message.getPriority())
                .status(message.getStatus())
                .cost(message.getCost())
                .build();
    }

    public MessageSummaryDTO toSummaryDTO(Message message){
        return MessageSummaryDTO.builder()
                .id(message.getId())
                .priority(message.getPriority())
                .cost(message.getCost())
                .content(message.getContent())
                .status(message.getStatus())
                .build();
    }
}
