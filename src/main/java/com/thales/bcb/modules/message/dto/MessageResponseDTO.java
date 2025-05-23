package com.thales.bcb.modules.message.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class MessageResponseDTO {
    private UUID id;
    private String status;
    private Instant estimatedDelivery;
    private BigDecimal cost;
    private BigDecimal currentBalance;
}
