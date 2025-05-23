package com.thales.bcb.modules.client.dto;

import com.thales.bcb.modules.client.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientBalanceResponseDTO {
    private PlanType planType;
    private BigDecimal balance;
    private BigDecimal limit;
}
