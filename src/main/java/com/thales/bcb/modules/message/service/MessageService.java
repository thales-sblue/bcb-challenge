package com.thales.bcb.modules.message.service;

import com.thales.bcb.modules.client.service.ClientService;
import com.thales.bcb.modules.message.dto.MessageRequestDTO;
import com.thales.bcb.modules.message.dto.MessageResponseDTO;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.mapper.MessageMapper;
import com.thales.bcb.modules.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ClientService clientService;
    private final MessageMapper messageMapper;

    public MessageResponseDTO sendMessage(UUID clientId, MessageRequestDTO request){

        BigDecimal currentBalance = clientService.processMessagePayment(clientId, request.getPriority());

        Message message = messageMapper.toEntity(request, clientId, currentBalance);
        messageRepository.save(message);

        return messageMapper.toResponse(message, currentBalance);
    }
}
