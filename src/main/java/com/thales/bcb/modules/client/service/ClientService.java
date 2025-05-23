package com.thales.bcb.modules.client.service;

import com.thales.bcb.modules.client.dto.ClientBalanceResponseDTO;
import com.thales.bcb.modules.client.dto.ClientRequestDTO;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.entity.Client;
import com.thales.bcb.modules.client.enums.PlanType;
import com.thales.bcb.modules.client.mapper.ClientMapper;
import com.thales.bcb.modules.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientResponseDTO create(ClientRequestDTO request){
        Client client = clientMapper.toEntity(request);

        client = clientRepository.save(client);

        return clientMapper.toResponse(client);
    }

    public ClientResponseDTO findById(UUID id){
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return clientMapper.toResponse(client);
    }

    public List<ClientResponseDTO> findAll(){
        return clientRepository.findAll().stream().map(clientMapper::toResponse).toList();
    }

    public ClientResponseDTO update(UUID id, ClientRequestDTO request){
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        clientMapper.updateEntityFromDto(request, client);

        clientRepository.save(client);

        return clientMapper.toResponse(client);
    }

    public ClientBalanceResponseDTO getBalance (UUID id){
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        return ClientBalanceResponseDTO.builder()
                .planType(client.getPlanType())
                .balance(client.getPlanType().equals(PlanType.PREPAID) ? client.getBalance() : null)
                .limit(client.getPlanType().equals(PlanType.POSTPAID) ? client.getLimit() : null)
                .build();
    }

    public void delete(UUID id){
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        clientRepository.delete(client);
    }
}
