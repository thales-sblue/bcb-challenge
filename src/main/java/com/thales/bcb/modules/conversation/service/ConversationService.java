package com.thales.bcb.modules.conversation.service;

import com.thales.bcb.modules.conversation.dto.ConversationResponseDTO;
import com.thales.bcb.modules.conversation.dto.ConversationSummaryDTO;
import com.thales.bcb.modules.conversation.entity.Conversation;
import com.thales.bcb.modules.conversation.mapper.ConversationMapper;
import com.thales.bcb.modules.conversation.repository.ConversationRepository;
import com.thales.bcb.modules.message.dto.MessageInConversationDTO;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.repository.MessageRepository;
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

    public List<ConversationSummaryDTO> listAllByClient(UUID clientId){
        return conversationRepository.findByClientId(clientId)
                .stream()
                .map(conversationMapper::toSummary)
                .collect(Collectors.toList());
    }

    public ConversationSummaryDTO findById (UUID conversationId){
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

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
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        boolean isParticipant = conversation.getClientId().equals(clientId) || conversation.getRecipientId().equals(clientId);
        if(!isParticipant){
            throw new RuntimeException("You dont have acess to this conversation");
        }

        List<Message> messages = messageRepository.findByConversationId(conversation.getId());

        return conversationMapper.toResponse(conversation, messages);

    }
}
