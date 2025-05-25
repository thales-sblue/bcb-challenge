package com.thales.bcb.modules.client.dto;

import com.thales.bcb.modules.client.enums.ClientDocumentType;
import com.thales.bcb.modules.client.enums.PlanType;
import com.thales.bcb.modules.client.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponseDTO {
    private String id;
    private String name;
    private String documentId;
    private ClientDocumentType documentType;
    private PlanType planType;
    private BigDecimal balance;
    private BigDecimal limit;
    private Boolean active;
    private Role role;
}
