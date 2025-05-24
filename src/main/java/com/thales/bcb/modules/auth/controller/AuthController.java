package com.thales.bcb.modules.auth.controller;

import com.thales.bcb.modules.auth.dto.AuthRequestDTO;
import com.thales.bcb.modules.auth.dto.AuthResponseDTO;
import com.thales.bcb.modules.auth.service.AuthService;
import com.thales.bcb.modules.client.entity.Client;
import com.thales.bcb.modules.client.repository.ClientRepository;
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

    @PostMapping
    public ResponseEntity<AuthResponseDTO> authenticate (@RequestBody AuthRequestDTO request){
        Client client = clientRepository.findByDocumentId(request.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Client not found or invalid credentials"));

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
