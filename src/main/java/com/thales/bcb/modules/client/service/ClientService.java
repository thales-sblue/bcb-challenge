package com.thales.bcb.modules.client.service;

import com.thales.bcb.modules.client.dto.ClientBalanceResponseDTO;
import com.thales.bcb.modules.client.dto.ClientRequestDTO;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.message.enums.Priority;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ClientService {

    ClientResponseDTO create(ClientRequestDTO request);

    ClientResponseDTO findByDocumentId(String documentId);

    ClientResponseDTO findById(UUID id);

    List<ClientResponseDTO> findAll();

    ClientResponseDTO update(UUID id, ClientRequestDTO request);

    ClientBalanceResponseDTO getBalance(UUID id);

    BigDecimal processMessagePayment(UUID clientId, Priority priority);

    BigDecimal getCostByPriority(Priority priority);

}
