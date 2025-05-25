package com.thales.bcb.modules.auth.controller;

import com.thales.bcb.exception.ResourceNotFoundException;
import com.thales.bcb.modules.auth.dto.AuthRequestDTO;
import com.thales.bcb.modules.auth.dto.AuthResponseDTO;
import com.thales.bcb.modules.auth.service.AuthService;
import com.thales.bcb.modules.client.entity.Client;
import com.thales.bcb.modules.client.repository.ClientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name ="Auth", description = "Endpoints de autenticação de clientes")
public class AuthController {

    private final ClientRepository clientRepository;
    private final AuthService authService;

    @Operation(summary = "Realiza autenticação", description = "Gera um token JWT válido por 30 minutos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida",
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(example = """
                            {
                              "timestamp": "2025-05-24T15:00:00",
                              "status": 404,
                              "error": "Not Found",
                              "message": "Client not found with document ID: 123456789",
                              "path": "/auth"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<AuthResponseDTO> authenticate (@RequestBody AuthRequestDTO request){
        Client client = clientRepository.findByDocumentId(request.getDocumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found or invalid credentials"));

        String token = authService.generateToken(client);
        long expiresIn = Duration.ofMinutes(30).toMillis();

        AuthResponseDTO authResponseDTO = AuthResponseDTO.builder()
                .accessToken(token)
                .expiresIn(expiresIn)
                .role(client.getRole().name())
                .build();

        return ResponseEntity.ok(authResponseDTO);
    }
}
