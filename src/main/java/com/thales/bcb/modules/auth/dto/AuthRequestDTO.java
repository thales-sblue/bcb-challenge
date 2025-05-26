package com.thales.bcb.modules.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @NotNull
    private String documentId;
}
