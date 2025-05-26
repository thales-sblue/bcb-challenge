package com.thales.bcb.modules.client.service;

import com.thales.bcb.exception.BusinessException;
import com.thales.bcb.exception.ResourceNotFoundException;
import com.thales.bcb.modules.client.dto.ClientBalanceResponseDTO;
import com.thales.bcb.modules.client.dto.ClientRequestDTO;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.entity.Client;
import com.thales.bcb.modules.client.enums.PlanType;
import com.thales.bcb.modules.client.mapper.ClientMapper;
import com.thales.bcb.modules.client.repository.ClientRepository;
import com.thales.bcb.modules.client.service.impl.ClientServiceImpl;
import com.thales.bcb.modules.message.enums.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private ClientRequestDTO requestDto;
    private ClientResponseDTO responseDto;
    private Client client;
    private UUID clientId;

    @BeforeEach
    void setup() {
        // Arrange common setup
        clientId = UUID.randomUUID();
        requestDto = new ClientRequestDTO(); // populate as needed
        responseDto = new ClientResponseDTO();
        client = mock(Client.class);
    }

    @Test
    void create_ShouldSaveAndReturnResponse() {
        // Arrange: stub mapper behavior
        when(clientMapper.toEntity(requestDto)).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(responseDto);

        // Act: call service
        ClientResponseDTO result = clientService.create(requestDto);

        // Assert: verify interactions and results
        verify(clientRepository).save(client);
        assertSame(responseDto, result);
    }

    @Test
    void findByDocumentId_Found_ShouldReturnResponse() {
        // Arrange
        String docId = "123";
        when(clientRepository.findByDocumentId(docId)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(responseDto);

        // Act
        ClientResponseDTO result = clientService.findByDocumentId(docId);

        // Assert
        assertSame(responseDto, result);
    }

    @Test
    void findByDocumentId_NotFound_ShouldThrow() {
        // Arrange
        when(clientRepository.findByDocumentId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> clientService.findByDocumentId("x"));
    }

    @Test
    void findById_Found_ShouldReturnResponse() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(responseDto);

        // Act
        ClientResponseDTO result = clientService.findById(clientId);

        // Assert
        assertSame(responseDto, result);
    }

    @Test
    void findById_NotFound_ShouldThrow() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> clientService.findById(clientId));
    }

    @Test
    void findAll_ShouldMapAll() {
        // Arrange
        Client other = new Client();
        List<Client> clients = List.of(client, other);
        when(clientRepository.findAll()).thenReturn(clients);
        when(clientMapper.toResponse(client)).thenReturn(responseDto);
        when(clientMapper.toResponse(other)).thenReturn(new ClientResponseDTO());

        // Act
        List<ClientResponseDTO> list = clientService.findAll();

        // Assert
        assertEquals(2, list.size());
        verify(clientMapper).toResponse(client);
        verify(clientMapper).toResponse(other);
    }

    @Test
    void update_Found_ShouldUpdateAndReturn() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateEntityFromDto(requestDto, client);
        when(clientMapper.toResponse(client)).thenReturn(responseDto);

        // Act
        ClientResponseDTO result = clientService.update(clientId, requestDto);

        // Assert
        verify(clientMapper).updateEntityFromDto(requestDto, client);
        verify(clientRepository).save(client);
        assertSame(responseDto, result);
    }

    @Test
    void update_NotFound_ShouldThrow() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> clientService.update(clientId, requestDto));
    }

    @Test
    void getBalance_Prepaid_ShouldReturnBalance() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(client.getPlanType()).thenReturn(PlanType.PREPAID);
        when(client.getBalance()).thenReturn(new BigDecimal("50.00"));

        // Act
        ClientBalanceResponseDTO dto = clientService.getBalance(clientId);

        // Assert
        assertEquals(PlanType.PREPAID, dto.getPlanType());
        assertEquals(new BigDecimal("50.00"), dto.getBalance());
        assertNull(dto.getLimit());
    }

    @Test
    void getBalance_Postpaid_ShouldReturnLimit() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(client.getPlanType()).thenReturn(PlanType.POSTPAID);
        when(client.getLimit()).thenReturn(new BigDecimal("100.00"));

        // Act
        ClientBalanceResponseDTO dto = clientService.getBalance(clientId);

        // Assert
        assertEquals(PlanType.POSTPAID, dto.getPlanType());
        assertNull(dto.getBalance());
        assertEquals(new BigDecimal("100.00"), dto.getLimit());
    }

    @Test
    void processMessagePayment_Inactive_ShouldThrow() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(client.getActive()).thenReturn(false);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> clientService.processMessagePayment(clientId, Priority.NORMAL));
    }

    @Test
    void processMessagePayment_Active_ShouldProcessAndReturnAmount() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(client.getActive()).thenReturn(true);
        BigDecimal cost = clientService.getCostByPriority(Priority.URGENT);
        BigDecimal available = new BigDecimal("77.77");
        when(client.getAvailableAmount()).thenReturn(available);

        // Act
        BigDecimal result = clientService.processMessagePayment(clientId, Priority.URGENT);

        // Assert
        verify(client).processPayment(cost);
        verify(clientRepository).save(client);
        assertEquals(available, result);
    }

    @Test
    void getCostByPriority_UrgentAndNormal() {
        // Arrange & Act & Assert
        assertEquals(new BigDecimal("0.50"), clientService.getCostByPriority(Priority.URGENT));
        assertEquals(new BigDecimal("0.25"), clientService.getCostByPriority(Priority.NORMAL));
    }
}
