package com.thales.bcb.modules.client.mapper;

import com.thales.bcb.modules.client.dto.ClientRequestDTO;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.entity.Client;
import com.thales.bcb.modules.client.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequestDTO request){
        return Client.builder()
                .name(request.getName())
                .documentId(request.getDocumentId())
                .documentType(request.getDocumentType())
                .planType(request.getPlanType())
                .balance(request.getBalance())
                .limit(request.getLimit())
                .active(request.getActive() != null ? request.getActive() : true)
                .role(request.getRole() != null ? request.getRole() : Role.CLIENT)
                .build();
    }

    public ClientResponseDTO toResponse(Client client){
        return ClientResponseDTO.builder()
                .id(client.getId().toString())
                .name(client.getName())
                .documentId(client.getDocumentId())
                .documentType(client.getDocumentType())
                .planType(client.getPlanType())
                .balance(client.getBalance())
                .limit(client.getLimit())
                .active(client.getActive())
                .role(client.getRole())
                .build();
    }

    public void updateEntityFromDto(ClientRequestDTO request, Client entity) {
        entity.setName(request.getName());
        entity.setDocumentId(request.getDocumentId());
        entity.setDocumentType(request.getDocumentType());
        entity.setPlanType(request.getPlanType());
        entity.setBalance(request.getBalance());
        entity.setLimit(request.getLimit());
        entity.setActive(request.getActive() != null ? request.getActive() : entity.getActive());
    }
}
