package com.thales.bcb.modules.message.service;

import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.service.ClientService;
import com.thales.bcb.modules.conversation.entity.Conversation;
import com.thales.bcb.modules.conversation.service.ConversationService;
import com.thales.bcb.modules.message.dto.MessageDTO;
import com.thales.bcb.modules.message.dto.MessageRequestDTO;
import com.thales.bcb.modules.message.dto.MessageResponseDTO;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.enums.Status;
import com.thales.bcb.modules.message.mapper.MessageMapper;
import com.thales.bcb.modules.message.repository.MessageRepository;
import com.thales.bcb.rabbitmq.publisher.MessagePublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

        MessageDTO dto = messageMapper.toDTO(message);
        messagePublisher.sendMessage(dto);

        return messageMapper.toResponse(message, currentBalance);
    }

    public void updateStatus(UUID messageId, Status status){
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));

        message.setStatus(status);
        messageRepository.save(message);
    }

    public MessageResponseDTO findById(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));
        return mapToResponse(message);
    }

    public List<MessageResponseDTO> findAll() {
        return messageRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<MessageResponseDTO> findByConversationId(UUID conversationId) {
        return messageRepository.findByConversationId(conversationId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<MessageResponseDTO> findBySenderId(UUID senderId) {
        return messageRepository.findBySenderId(senderId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<MessageResponseDTO> findByRecipientId(UUID recipientId) {
        return messageRepository.findByRecipientId(recipientId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MessageResponseDTO mapToResponse(Message message) {
        ClientResponseDTO client = clientService.findById(message.getSenderId());
        return messageMapper.toResponse(message, client.getBalance());
    }

}
