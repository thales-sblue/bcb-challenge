package com.thales.bcb.modules.conversation.service;

import com.thales.bcb.modules.conversation.dto.ConversationResponseDTO;
import com.thales.bcb.modules.conversation.dto.ConversationSummaryDTO;
import com.thales.bcb.modules.conversation.entity.Conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ConversationService {

    List<ConversationSummaryDTO> listAllByClient(UUID clientId);

    ConversationSummaryDTO findById(UUID conversationId);

    Conversation getOrCreateConversation(UUID clientId, UUID recipientId, String recipientName, String content, Instant lastMessageTime);

    void updateConversation(Conversation conversation, String lastMessageContent);

    ConversationResponseDTO getConversationWithMessages(UUID conversationId, UUID clientId);

    void updateUnreadCount(UUID conversationId, Integer successCount);
}
