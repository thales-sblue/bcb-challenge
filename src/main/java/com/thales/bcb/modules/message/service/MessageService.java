package com.thales.bcb.modules.message.service;

import com.thales.bcb.modules.message.dto.*;
import com.thales.bcb.modules.message.enums.Priority;
import com.thales.bcb.modules.message.enums.Status;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponseDTO sendMessage(UUID clientId, MessageRequestDTO request);

    void updateStatus(UUID messageId, Status status);

    MessageSummaryDTO findById(UUID messageId);

    List<MessageSummaryDTO> findAll();

    List<MessageDTO> findByConversationId(UUID conversationId);

    List<MessageSummaryDTO> listMessages(Priority priority, Status status);


    List<UUID> findMessageIdsByConversationIdAndRecipientIdAndStatus(UUID conversationId,UUID clientId, Status status);

    List<UUID> markMessagesAsRead(ReadStatusPayload readStatusPayload);

    int countUnreadMessagesByConversation(UUID conversationId);

}
