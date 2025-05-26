package com.thales.bcb.modules.message.dto;

import com.thales.bcb.modules.client.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDTO {
    private UUID id;
    private String status;
    private Instant estimatedDelivery;
    private BigDecimal cost;
    private BigDecimal currentBalance;
}
