package com.thales.bcb.modules.message.controller;

import com.thales.bcb.modules.message.dto.MessageRequestDTO;
import com.thales.bcb.modules.message.dto.MessageResponseDTO;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.mapper.MessageMapper;
import com.thales.bcb.modules.message.service.MessageService;
import com.thales.bcb.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
@Tag(name ="Messages", description = "Endpoints de gerenciamento de mensagens")
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

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

    @Operation(summary = "Consultar mensagem por ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensagem retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> getById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(messageService.findById(id));
    }

    @Operation(summary = "Consultar status de uma mensagem.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensagem retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @GetMapping("/{id}/status")
    public String getStatus(@PathVariable UUID id){
        return messageService.findById(id).getStatus();
    }

    @Operation(summary = "Consultar todas as mensagens ou mensagens com filtro conversationId, senderId ou recipientId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensagem retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @GetMapping()
    public List<MessageResponseDTO> listMessages(
            @RequestParam(required = false) UUID conversationId,
            @RequestParam(required = false) UUID senderId,
            @RequestParam(required = false) UUID recipientId
    ) {
        if (conversationId != null) {
            return messageService.findByConversationId(conversationId);
        } else if (senderId != null) {
            return messageService.findBySenderId(senderId);
        } else if (recipientId != null) {
            return messageService.findByRecipientId(recipientId);
        } else {
            return messageService.findAll();
        }
    }
}
