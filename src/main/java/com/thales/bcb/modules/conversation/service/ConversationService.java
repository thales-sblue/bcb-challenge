package com.thales.bcb.modules.conversation.service;

import com.thales.bcb.modules.conversation.dto.ConversationResponseDTO;
import com.thales.bcb.modules.conversation.entity.Conversation;
import com.thales.bcb.modules.conversation.mapper.ConversationMapper;
import com.thales.bcb.modules.conversation.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;

    public List<ConversationResponseDTO> listAllByClient(UUID clientId){
        return conversationRepository.findByClientId(clientId)
                .stream()
                .map(conversationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ConversationResponseDTO findById (UUID conversationId){
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        return conversationMapper.toResponse(conversation);
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
}
