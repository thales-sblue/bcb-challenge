package com.thales.bcb.modules.client.controller;

import com.thales.bcb.modules.client.dto.ClientBalanceResponseDTO;
import com.thales.bcb.modules.client.dto.ClientRequestDTO;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name ="Clients", description = "Endpoints de gerenciamento de clientes")
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Criar um novo cliente", description = "Cria um cliente com saldo inicial e informações cadastrais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping()
    public ResponseEntity<ClientResponseDTO> create(@RequestBody ClientRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(request));
    }

    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados de um cliente específico através do ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> findById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(clientService.findById(id));
    }

    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista de todos os clientes cadastrados. Acesso restrito para ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClientResponseDTO>> findAll(){
        return ResponseEntity.ok(clientService.findAll());
    }

    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Atualizar cadastro do cliente", description = "Atualiza as informações de um cliente específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable UUID id, @RequestBody ClientRequestDTO request){
        return ResponseEntity.ok(clientService.update(id, request));
    }

    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Consultar saldo do cliente", description = "Retorna o saldo disponível do cliente informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<ClientBalanceResponseDTO> getBalance (@PathVariable UUID id) {
        return ResponseEntity.ok().body(clientService.getBalance(id));
    }
}

















