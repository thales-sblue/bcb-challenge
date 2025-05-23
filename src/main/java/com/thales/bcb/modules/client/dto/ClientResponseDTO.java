package com.thales.bcb.modules.client.dto;

import com.thales.bcb.modules.client.enums.DocumentType;
import com.thales.bcb.modules.client.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ClientResponseDTO {
    private String id;
    private String name;
    private String documentId;
    private DocumentType documentType;
    private PlanType planType;
    private BigDecimal balance;
    private BigDecimal limit;
    private Boolean active;
}
