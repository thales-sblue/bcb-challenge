package com.thales.bcb.modules.message.controller;

import com.thales.bcb.modules.message.dto.MessageRequestDTO;
import com.thales.bcb.modules.message.dto.MessageResponseDTO;
import com.thales.bcb.modules.message.service.MessageService;
import com.thales.bcb.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
@Tag(name ="Messages", description = "Endpoints de gerenciamento de mensagens")
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Enviar nova mensagem.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensagem enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping()
    public ResponseEntity<MessageResponseDTO> sendMessage(@RequestBody MessageRequestDTO request){
        UUID clientId = SecurityUtil.getClientIdFromToken();
        MessageResponseDTO response = messageService.sendMessage(clientId, request);
        return ResponseEntity.status(201).body(response);
    }

}
