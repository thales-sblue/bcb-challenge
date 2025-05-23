package com.thales.bcb.modules.client.controller;

import com.thales.bcb.modules.client.dto.ClientRequestDTO;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name ="Clients", description = "Endpoints de gerenciamento de clientes")
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Cria um novo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@RequestBody ClientRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(request));
    }

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente nao encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> findById(@PathVariable UUID id){
        System.out.println("ID CHEGANDO AQUI" + id);
        return ResponseEntity.status(HttpStatus.OK).body(clientService.findById(id));
    }


}

















