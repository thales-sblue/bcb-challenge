package com.thales.bcb.modules.conversation.controller;

import com.thales.bcb.modules.conversation.dto.ConversationResponseDTO;
import com.thales.bcb.modules.conversation.dto.ConversationSummaryDTO;
import com.thales.bcb.modules.conversation.service.ConversationService;
import com.thales.bcb.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
@Tag(name ="Conversas", description = "Endpoints de gerenciamento de conversas")
@SecurityRequirement(name = "jwt_auth")
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(
            summary = "Listar todas as conversas do cliente autenticado",
            description = "Retorna um resumo de todas as conversas pertencentes ao cliente autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversas listadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping()
    public List<ConversationSummaryDTO> listConversations(){
        UUID clientId = SecurityUtil.getClientIdFromToken();
        return conversationService.listAllByClient(clientId);
    }

    @Operation(
            summary = "Buscar uma conversa específica",
            description = "Busca uma conversa pelo seu ID. Apenas participantes da conversa podem acessá-la."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conversa não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/{id}")
    public ConversationSummaryDTO getConversation(@PathVariable UUID id){
        return conversationService.findById(id);
    }

    @Operation(
            summary = "Listar mensagens de uma conversa",
            description = "Retorna todas as mensagens de uma conversa. Apenas participantes podem acessar as mensagens."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagens listadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conversa não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado à conversa"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })

    @GetMapping("/{id}/messages")
    public ConversationResponseDTO getConversationWithMessages(@PathVariable UUID id){
        UUID clientId = SecurityUtil.getClientIdFromToken();
        return conversationService.getConversationWithMessages(id, clientId);
    }
}
