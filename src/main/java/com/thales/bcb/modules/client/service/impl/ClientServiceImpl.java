package com.thales.bcb.modules.client.service.impl;

import com.thales.bcb.exception.BusinessException;
import com.thales.bcb.exception.ResourceNotFoundException;
import com.thales.bcb.modules.client.dto.ClientBalanceResponseDTO;
import com.thales.bcb.modules.client.dto.ClientRequestDTO;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.entity.Client;
import com.thales.bcb.modules.client.enums.PlanType;
import com.thales.bcb.modules.client.mapper.ClientMapper;
import com.thales.bcb.modules.client.repository.ClientRepository;
import com.thales.bcb.modules.client.service.ClientService;
import com.thales.bcb.modules.message.enums.Priority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    @Transactional
    public ClientResponseDTO create(ClientRequestDTO request) {
        Client client = clientMapper.toEntity(request);
        clientRepository.save(client);
        return clientMapper.toResponse(client);
    }


    @Override
    public ClientResponseDTO findByDocumentId(String documentId) {
        Client client = clientRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(""));

        return clientMapper.toResponse(client);
    }

    @Override
    public ClientResponseDTO findById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found " + id));
        return clientMapper.toResponse(client);
    }

    @Override
    public List<ClientResponseDTO> findAll() {
        return clientRepository.findAll().stream().map(clientMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ClientResponseDTO update(UUID id, ClientRequestDTO request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found" + id));

        clientMapper.updateEntityFromDto(request, client);

        clientRepository.save(client);

        return clientMapper.toResponse(client);
    }

    @Override
    public ClientBalanceResponseDTO getBalance(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found" + id));

        return ClientBalanceResponseDTO.builder()
                .planType(client.getPlanType())
                .balance(client.getPlanType().equals(PlanType.PREPAID) ? client.getBalance() : null)
                .limit(client.getPlanType().equals(PlanType.POSTPAID) ? client.getLimit() : null)
                .build();
    }

    @Override
    @Transactional
    public BigDecimal processMessagePayment(UUID clientId, Priority priority) {
        var client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found" + clientId));

        if (!client.getActive()) {
            throw new BusinessException("Client with ID " + clientId + " is inactive");
        }

        BigDecimal cost = getCostByPriority(priority);
        client.processPayment(cost);
        clientRepository.save(client);

        return client.getAvailableAmount();
    }

    @Override
    public BigDecimal getCostByPriority(Priority priority) {
        return priority == Priority.URGENT
                ? new BigDecimal("0.50")
                : new BigDecimal("0.25");
    }


}
