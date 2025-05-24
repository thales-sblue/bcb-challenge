package com.thales.bcb.modules.conversation.controller;

import com.thales.bcb.modules.conversation.dto.ConversationResponseDTO;
import com.thales.bcb.modules.conversation.dto.ConversationSummaryDTO;
import com.thales.bcb.modules.conversation.service.ConversationService;
import com.thales.bcb.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(summary = "Listar conversas por usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversas listadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @GetMapping()
    public List<ConversationSummaryDTO> listConversations(){
        UUID clientId = SecurityUtil.getClientIdFromToken();
        return conversationService.listAllByClient(clientId);
    }

    @Operation(summary = "Buscar conversa pelo seu id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversa encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @GetMapping("/{id}")
    public ConversationSummaryDTO getConversation(@PathVariable UUID id){
        return conversationService.findById(id);
    }

    @Operation(summary = "Listar mensagens de uma conversa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversa encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @GetMapping("/{id}/messages")
    public ConversationResponseDTO getConversationWithMessages(@PathVariable UUID id){
        UUID clientId = SecurityUtil.getClientIdFromToken();
        return conversationService.getConversationWithMessages(id, clientId);
    }
}
