package com.thales.bcb.rabbitmq.controller;

import com.thales.bcb.rabbitmq.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@Tag(name = "Queue", description = "Endpoints de monitoramento da fila RabbitMQ")
public class QueueController {

    private final QueueService queueService;

    @Operation(summary = "Verificar status da fila RabbitMQ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status da fila retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado - Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar status da fila")
    })
    @GetMapping("/status")
    public Map<String, Object> getQueueStatus() {
        return queueService.getQueueStatus();
    }
}
