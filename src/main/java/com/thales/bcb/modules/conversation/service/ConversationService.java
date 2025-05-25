package com.thales.bcb.modules.conversation.service;

import com.thales.bcb.exception.BusinessException;
import com.thales.bcb.exception.ResourceNotFoundException;
import com.thales.bcb.modules.conversation.dto.ConversationResponseDTO;
import com.thales.bcb.modules.conversation.dto.ConversationSummaryDTO;
import com.thales.bcb.modules.conversation.entity.Conversation;
import com.thales.bcb.modules.conversation.mapper.ConversationMapper;
import com.thales.bcb.modules.conversation.repository.ConversationRepository;
import com.thales.bcb.modules.message.dto.ReadStatusPayload;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.enums.Status;
import com.thales.bcb.modules.message.repository.MessageRepository;
import com.thales.bcb.rabbitmq.publisher.ReadStatusPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;
    private final MessageRepository messageRepository;
    private final ReadStatusPublisher readStatusPublisher;

    public List<ConversationSummaryDTO> listAllByClient(UUID clientId){
        return conversationRepository.findByClientId(clientId)
                .stream()
                .map(conversationMapper::toSummary)
                .collect(Collectors.toList());
    }

    public ConversationSummaryDTO findById (UUID conversationId){
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found " + conversationId));

        return conversationMapper.toSummary(conversation);
    }


    public Conversation getOrCreateConversation(UUID clientId, UUID recipientId, String recipientName){
         return conversationRepository.findByParticipants(clientId, recipientId)
                .orElseGet(() -> {
                    Conversation conversation = conversationMapper.toEntity(clientId, recipientId, recipientName);
                    return conversationRepository.save(conversation);
                });
    }

    public void updateConversation(Conversation conversation, String lastMessageContent){
        conversation.setLastMessageContent(lastMessageContent);
        conversation.setLastMessageTime(Instant.now());
        conversation.setUnreadCount(conversation.getUnreadCount() + 1);

        conversationRepository.save(conversation);
    }

    public ConversationResponseDTO getConversationWithMessages(UUID conversationId, UUID clientId){
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found " + conversationId));

        boolean isParticipant = conversation.getClientId().equals(clientId) || conversation.getRecipientId().equals(clientId);
        if(!isParticipant){
            throw new BusinessException("You don't have access to this conversation");
        }

        List<UUID> unreadMessages = messageRepository.findMessageIdsByConversationIdAndRecipientIdAndStatus(conversationId, clientId, Status.DELIVERED);
        if(!unreadMessages.isEmpty()){
            ReadStatusPayload readStatusPayload = ReadStatusPayload.builder()
                            .conversationId(conversationId)
                            .readerId(clientId)
                            .messageIds(unreadMessages)
                            .build();

            readStatusPublisher.sendReadStatus(readStatusPayload);
        }

        List<Message> messages = messageRepository.findByConversationId(conversation.getId());
        return conversationMapper.toResponse(conversation, messages);
    }

    public void updateUnreadCount(UUID conversationId, int successCount) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found " + conversationId));

        int currentUnread = conversation.getUnreadCount();
        int updatedUnread = Math.max(0, currentUnread - successCount);

        conversation.setUnreadCount(updatedUnread);
        conversationRepository.save(conversation);
    }
}
