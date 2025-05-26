package com.thales.bcb.modules.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.thales.bcb.modules.auth.dto.AuthResponseDTO;
import com.thales.bcb.modules.client.dto.ClientResponseDTO;

public interface AuthService {

    AuthResponseDTO generateToken(String clientId);

    DecodedJWT validateToken(String token);
}
