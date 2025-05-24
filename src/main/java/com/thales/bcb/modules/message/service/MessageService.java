package com.thales.bcb.modules.message.service;

import com.thales.bcb.modules.client.service.ClientService;
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
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ClientService clientService;
    private final MessageMapper messageMapper;
    private final MessagePublisher messagePublisher;

    @Transactional
    public MessageResponseDTO sendMessage(UUID clientId, MessageRequestDTO request){

        BigDecimal currentBalance = clientService.processMessagePayment(clientId, request.getPriority());

        Message message = messageMapper.toEntity(request, clientId, currentBalance);
        messageRepository.save(message);

        MessageDTO dto = MessageDTO.builder()
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
        messagePublisher.sendMessage(dto);

        return messageMapper.toResponse(message, currentBalance);
    }

    public void updateStatus(UUID messageId, Status status){
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));

        message.setStatus(status);
        messageRepository.save(message);
    }

    public MessageResponseDTO findById(UUID messageId){
        Message message = messageRepository.findById(messageId)
                .orElseThrow(()-> new RuntimeException("Mensagem não encontrada"));

        return messageMapper.toResponse(message, message.getCost());
    }


    public List<MessageResponseDTO> findAll() {
        return messageRepository.findAll().stream()
                .map(message -> messageMapper.toResponse(message, message.getCost()))
                .toList();
    }

    public List<MessageResponseDTO> findByConversationId(UUID conversationId) {
        return messageRepository.findByConversationId(conversationId).stream()
                .map(message -> messageMapper.toResponse(message, message.getCost()))
                .toList();
    }

    public List<MessageResponseDTO> findBySenderId(UUID senderId) {
        return messageRepository.findBySenderId(senderId).stream()
                .map(message -> messageMapper.toResponse(message, message.getCost()))
                .toList();
    }

    public List<MessageResponseDTO> findByRecipientId(UUID recipientId) {
        return messageRepository.findByRecipientId(recipientId).stream()
                .map(message -> messageMapper.toResponse(message, message.getCost()))
                .toList();
    }

}
