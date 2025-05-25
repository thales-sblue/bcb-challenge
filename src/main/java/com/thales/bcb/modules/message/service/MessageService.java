package com.thales.bcb.modules.message.service;

import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.service.ClientService;
import com.thales.bcb.modules.conversation.entity.Conversation;
import com.thales.bcb.modules.conversation.service.ConversationService;
import com.thales.bcb.modules.message.dto.*;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.enums.Status;
import com.thales.bcb.modules.message.mapper.MessageMapper;
import com.thales.bcb.modules.message.repository.MessageRepository;
import com.thales.bcb.rabbitmq.publisher.MessagePublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.cfg.defs.UUIDDef;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final ClientService clientService;
    private final MessageMapper messageMapper;
    private final MessagePublisher messagePublisher;
    private final ConversationService conversationService;

    @Transactional
    public MessageResponseDTO sendMessage(UUID clientId, MessageRequestDTO request){

        BigDecimal currentBalance = clientService.processMessagePayment(clientId, request.getPriority());

        ClientResponseDTO recipient = clientService.findById(request.getRecipientId());
        Conversation conversation = conversationService.getOrCreateConversation(
                clientId,
                UUID.fromString(recipient.getId()),
                recipient.getName());

        conversationService.updateConversation(conversation, request.getContent());
        request.setConversationId(conversation.getId());

        Message message = messageMapper.toEntity(request, clientId, clientService.getCostByPriority(request.getPriority()));
        messageRepository.save(message);

        updateStatus(message.getId(), Status.PROCESSING);
        MessageDTO dto = messageMapper.toDTO(message);
        messagePublisher.sendMessage(dto);
        updateStatus(message.getId(), Status.SENT);

        return messageMapper.toResponse(message, currentBalance);
    }

    public void updateStatus(UUID messageId, Status status){
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));

        message.setStatus(status);
        messageRepository.save(message);
    }

    public MessageSummaryDTO findById(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));
        return mapToSummary(message);
    }

    public List<MessageSummaryDTO> findAll() {
        return messageRepository.findAll().stream()
                .map(this::mapToSummary)
                .toList();
    }

    public List<MessageSummaryDTO> findByConversationId(UUID conversationId) {
        return messageRepository.findByConversationId(conversationId).stream()
                .map(this::mapToSummary)
                .toList();
    }

    public List<MessageSummaryDTO> findBySenderId(UUID senderId) {
        return messageRepository.findBySenderId(senderId).stream()
                .map(this::mapToSummary)
                .toList();
    }

    public List<MessageSummaryDTO> findByRecipientId(UUID recipientId) {
        return messageRepository.findByRecipientId(recipientId).stream()
                .map(this::mapToSummary)
                .toList();
    }

    private MessageSummaryDTO mapToSummary(Message message) {
        return messageMapper.toSummaryDTO(message);
    }

    public List<UUID> markMessagesAsRead(ReadStatusPayload readStatusPayload){
        List<UUID> failedMessages = new ArrayList<>();

        readStatusPayload.getMessageIds().forEach(messageId -> {
            messageRepository.findById(messageId).ifPresentOrElse(
                    message -> {
                        message.setStatus(Status.READ);
                        messageRepository.save(message);
                        log.info("Mensagem {} marcada como lida", message.getId());
                    },
                    () -> {
                        log.warn("Mensagem {} não encontrada e não marcada como lida.", messageId);
                        failedMessages.add(messageId);
                    }
            );
        });
        return failedMessages;
    }
}
