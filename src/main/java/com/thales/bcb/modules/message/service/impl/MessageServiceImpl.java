package com.thales.bcb.modules.message.service.impl;

import com.thales.bcb.exception.ResourceNotFoundException;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.service.ClientService;
import com.thales.bcb.modules.conversation.entity.Conversation;
import com.thales.bcb.modules.conversation.service.ConversationService;
import com.thales.bcb.modules.message.dto.*;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.enums.Priority;
import com.thales.bcb.modules.message.enums.Status;
import com.thales.bcb.modules.message.mapper.MessageMapper;
import com.thales.bcb.modules.message.repository.MessageRepository;
import com.thales.bcb.modules.message.service.MessageService;
import com.thales.bcb.rabbitmq.publisher.MessagePublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ClientService clientService;
    private final MessageMapper messageMapper;
    private final MessagePublisher messagePublisher;
    private final ConversationService conversationService;

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(UUID clientId, MessageRequestDTO request) {

        log.info("[MessageServiceImpl] Starting processMessagePayment");
        BigDecimal currentBalance = clientService.processMessagePayment(clientId, request.getPriority());

        ClientResponseDTO recipient = clientService.findById(request.getRecipientId());
        Conversation conversation = conversationService.getOrCreateConversation(
                clientId,
                UUID.fromString(recipient.getId()),
                recipient.getName(),
                request.getContent(),
                Instant.now());

        log.info("[MessageServiceImpl] Starting updateConversation");
        conversationService.updateConversation(conversation, request.getContent());
        request.setConversationId(conversation.getId());

        log.info("[MessageServiceImpl] Starting save message");
        Message message = messageMapper.toEntity(request, clientId, clientService.getCostByPriority(request.getPriority()));
        messageRepository.save(message);

        updateStatus(message.getId(), Status.PROCESSING);
        MessageDTO dto = messageMapper.toDTO(message);
        messagePublisher.sendMessage(dto);
        updateStatus(message.getId(), Status.SENT);

        return messageMapper.toResponse(message, currentBalance);
    }

    @Override
    public void updateStatus(UUID messageId, Status status) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found " + messageId));

        message.setStatus(status);
        messageRepository.save(message);
    }

    @Override
    public MessageSummaryDTO findById(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found " + messageId));
        return messageMapper.toSummaryDTO(message);
    }

    @Override
    public List<MessageSummaryDTO> findAll() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public List<MessageDTO> findByConversationId(UUID conversationId) {
        return messageRepository.findByConversationId(conversationId).stream()
                .map(messageMapper::toDTO)
                .toList();
    }

    @Override
    public List<UUID> findMessageIdsByConversationIdAndRecipientIdAndStatus(UUID conversationId, UUID clientId, Status status) {
        return messageRepository.findMessageIdsByConversationIdAndRecipientIdAndStatus(conversationId, clientId, status);
    }

    @Override
    public List<MessageSummaryDTO> listMessages(Priority priority, Status status) {
        List<Message> messages;

        if (priority != null && status != null) {
            messages = messageRepository.findByPriorityAndStatus(priority, status);
        } else if (priority != null) {
            messages = messageRepository.findByPriority(priority);
        } else if (status != null) {
            messages = messageRepository.findByStatus(status);
        } else {
            messages = messageRepository.findAll();
        }

        return messages.stream()
                .map(messageMapper::toSummaryDTO)
                .toList();
    }


    @Override
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

    @Override
    public int countUnreadMessagesByConversation(UUID conversationId) {
        return messageRepository.countByConversationIdAndStatus(conversationId, Status.QUEUED);
    }
}
