package com.thales.bcb.modules.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private String accessToken;
    private Long expiresIn;
    private String role;
}
