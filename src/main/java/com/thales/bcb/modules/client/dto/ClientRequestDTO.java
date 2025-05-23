package com.thales.bcb.modules.client.dto;

import com.thales.bcb.modules.client.enums.DocumentType;
import com.thales.bcb.modules.client.enums.PlanType;
import com.thales.bcb.modules.client.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ClientRequestDTO {
    private String name;
    private String documentId;
    private DocumentType documentType;
    private PlanType planType;
    private BigDecimal balance;
    private BigDecimal limit;
    private Boolean active;
    private Role role;
}
