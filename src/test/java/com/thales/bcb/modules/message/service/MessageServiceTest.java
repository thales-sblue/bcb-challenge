package com.thales.bcb.modules.message.service;

import com.thales.bcb.exception.ResourceNotFoundException;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.enums.ClientDocumentType;
import com.thales.bcb.modules.client.enums.PlanType;
import com.thales.bcb.modules.client.enums.Role;
import com.thales.bcb.modules.client.service.ClientService;
import com.thales.bcb.modules.conversation.entity.Conversation;
import com.thales.bcb.modules.conversation.service.ConversationService;
import com.thales.bcb.modules.message.dto.MessageRequestDTO;
import com.thales.bcb.modules.message.dto.MessageResponseDTO;
import com.thales.bcb.modules.message.dto.MessageSummaryDTO;
import com.thales.bcb.modules.message.dto.ReadStatusPayload;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.enums.Priority;
import com.thales.bcb.modules.message.enums.Status;
import com.thales.bcb.modules.message.mapper.MessageMapper;
import com.thales.bcb.modules.message.repository.MessageRepository;
import com.thales.bcb.rabbitmq.publisher.MessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private ConversationService conversationService;

    private UUID clientId;
    private UUID recipientId;
    private UUID messageId;
    private MessageRequestDTO request;
    private Conversation conversation;
    private Message message;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        recipientId = UUID.randomUUID();
        messageId = UUID.randomUUID();

        request = new MessageRequestDTO();
        request.setRecipientId(recipientId);
        request.setContent("Hello!");
        request.setPriority(Priority.NORMAL);

        conversation = new Conversation();
        conversation.setId(UUID.randomUUID());

        message = new Message();
        message.setId(messageId);
        message.setStatus(Status.QUEUED);
    }

    @Test
    void testSendMessageSuccess() {
        // Mock comportamento dos serviços
        when(clientService.processMessagePayment(clientId, Priority.NORMAL))
                .thenReturn(BigDecimal.valueOf(9.75));

        when(clientService.findById(recipientId))
                .thenReturn(ClientResponseDTO.builder()
                        .id(recipientId.toString())
                        .name("Recipient")
                        .documentId("12345678900")
                        .documentType(ClientDocumentType.CPF)
                        .planType(PlanType.PREPAID)
                        .balance(BigDecimal.valueOf(10))
                        .limit(BigDecimal.ZERO)
                        .active(true)
                        .role(Role.CLIENT)
                        .build());

        when(conversationService.getOrCreateConversation(eq(clientId), eq(recipientId), eq("Recipient")))
                .thenReturn(conversation);

        when(messageMapper.toEntity(any(), eq(clientId), any()))
                .thenReturn(message);

        // Mock necessário para não dar ResourceNotFound nas atualizações de status
        when(messageRepository.findById(message.getId()))
                .thenReturn(Optional.of(message));

        when(messageRepository.save(any())).thenReturn(message);

        when(messageMapper.toResponse(eq(message), any()))
                .thenReturn(MessageResponseDTO.builder()
                        .id(messageId)
                        .status(Status.SENT.name())
                        .estimatedDelivery(Instant.now())
                        .cost(BigDecimal.valueOf(0.25))
                        .currentBalance(BigDecimal.valueOf(9.75))
                        .build());

        // Executa
        MessageResponseDTO response = messageService.sendMessage(clientId, request);

        // Verifica
        assertNotNull(response);
        assertEquals(messageId, response.getId());
        assertEquals(Status.SENT.name(), response.getStatus());

        verify(messagePublisher, times(1)).sendMessage(any());
        verify(messageRepository, times(3)).save(any()); // Salvar inicial + status PROCESSING + status SENT
        verify(conversationService).updateConversation(conversation, "Hello!");
    }

    @Test
    void testUpdateStatusMessageNotFound() {
        UUID id = UUID.randomUUID();
        when(messageRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> messageService.updateStatus(id, Status.SENT));
    }

    @Test
    void testFindByIdSuccess() {
        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        when(messageMapper.toSummaryDTO(message))
                .thenReturn(new MessageSummaryDTO(messageId, Priority.NORMAL, BigDecimal.valueOf(0.25), "Test!", Status.SENT));

        var result = messageService.findById(messageId);

        assertNotNull(result);
        assertEquals(messageId, result.getId());
        assertEquals("Test!", result.getContent());
        assertEquals(Status.SENT, result.getStatus());

        verify(messageRepository).findById(messageId);
    }

    @Test
    void testFindByIdNotFound() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> messageService.findById(messageId));
    }

    @Test
    void testMarkMessagesAsRead() {
        UUID messageId1 = UUID.randomUUID();
        UUID messageId2 = UUID.randomUUID();

        Message message1 = new Message();
        message1.setId(messageId1);
        message1.setStatus(Status.QUEUED);

        when(messageRepository.findById(messageId1)).thenReturn(Optional.of(message1));
        when(messageRepository.findById(messageId2)).thenReturn(Optional.empty());

        var payload = new ReadStatusPayload();
        payload.setMessageIds(List.of(messageId1, messageId2));

        var failed = messageService.markMessagesAsRead(payload);

        assertEquals(1, failed.size());
        assertTrue(failed.contains(messageId2));
        assertEquals(Status.READ, message1.getStatus());

        verify(messageRepository).save(message1);
    }

    @Test
    void testFindAllMessages() {
        when(messageRepository.findAll()).thenReturn(List.of(message));

        when(messageMapper.toSummaryDTO(message))
                .thenReturn(new MessageSummaryDTO(messageId, Priority.NORMAL, BigDecimal.valueOf(0.25), "Test!", Status.SENT));
        var result = messageService.findAll();

        assertEquals(1, result.size());
        assertEquals(messageId, result.get(0).getId());

        verify(messageRepository).findAll();
    }

    @Test
    void testSendMessageThrowsBusinessException() {
        when(clientService.processMessagePayment(clientId, Priority.NORMAL))
                .thenThrow(new RuntimeException("Saldo insuficiente"));

        assertThrows(RuntimeException.class, () -> messageService.sendMessage(clientId, request));

        verify(messageRepository, never()).save(any());
        verify(messagePublisher, never()).sendMessage(any());
    }

}
