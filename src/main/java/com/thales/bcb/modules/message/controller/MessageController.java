package com.thales.bcb.modules.message.controller;

import com.thales.bcb.modules.message.dto.MessageRequestDTO;
import com.thales.bcb.modules.message.dto.MessageResponseDTO;
import com.thales.bcb.modules.message.dto.MessageSummaryDTO;
import com.thales.bcb.modules.message.enums.Priority;
import com.thales.bcb.modules.message.enums.Status;
import com.thales.bcb.modules.message.service.MessageService;
import com.thales.bcb.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
@Tag(name ="Mensagens", description = "Endpoints de gerenciamento de mensagens")
@SecurityRequirement(name = "jwt_auth")
public class MessageController {

    private final MessageService messageService;

    @Operation(
            summary = "Enviar uma nova mensagem",
            description = "Envia uma mensagem para um destinatário, cria a conversa caso não exista e realiza a cobrança baseada no plano do remetente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensagem enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo/limite insuficiente"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação ou dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping()
    public ResponseEntity<MessageResponseDTO> sendMessage(@RequestBody MessageRequestDTO request){
        UUID clientId = SecurityUtil.getClientIdFromToken();
        MessageResponseDTO response = messageService.sendMessage(clientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Buscar uma mensagem por ID",
            description = "Retorna os dados de uma mensagem específica através do seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagem encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mensagem não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MessageSummaryDTO> getById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(messageService.findById(id));
    }

    @Operation(
            summary = "Consultar o status de uma mensagem",
            description = "Retorna o status atual de uma mensagem (SENT, DELIVERED, READ, etc)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mensagem não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/{id}/status")
    public Status getStatus(@PathVariable UUID id){
        return messageService.findById(id).getStatus();
    }

    @Operation(
            summary = "Listar mensagens",
            description = "Retorna uma lista de mensagens. Pode ser filtrada por priority e status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagens retornadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping()
    public List<MessageSummaryDTO> listMessages(
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status status
    ) {
        return messageService.listMessages(priority, status);
    }

}
