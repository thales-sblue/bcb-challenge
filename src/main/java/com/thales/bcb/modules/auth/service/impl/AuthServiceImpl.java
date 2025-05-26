package com.thales.bcb.modules.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.thales.bcb.exception.InvalidJwtAuthenticationException;
import com.thales.bcb.modules.auth.dto.AuthResponseDTO;
import com.thales.bcb.modules.auth.service.AuthService;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;
import com.thales.bcb.modules.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final ClientService clientService;

    @Override
    public AuthResponseDTO generateToken(String clientId) {

        ClientResponseDTO client = clientService.findByDocumentId(clientId);

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(30));

        String token =  JWT.create()
                .withIssuer("bcb-api")
                .withSubject(client.getId())
                .withClaim("clientId", client.getId())
                .withClaim("documentId", client.getDocumentId())
                .withClaim("role", client.getRole().name())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
        long expiresIn = Duration.ofMinutes(30).toMillis();

        return AuthResponseDTO.builder()
                .accessToken(token)
                .expiresIn(expiresIn)
                .role(client.getRole().name())
                .build();
    }

    @Override
    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("bcb-api")
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new InvalidJwtAuthenticationException("Invalid or expired token " + token);
        }
    }

}
