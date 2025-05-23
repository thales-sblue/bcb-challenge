package com.thales.bcb.modules.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.thales.bcb.modules.client.entity.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(Client client) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(30));

        return JWT.create()
                .withIssuer("bcb-api")
                .withSubject(client.getId().toString())
                .withClaim("clientId", client.getId().toString())
                .withClaim("documentId", client.getDocumentId())
                .withClaim("role",client.getRole().name())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("bcb-api")
                    .build();
            return verifier.verify(token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }

}
