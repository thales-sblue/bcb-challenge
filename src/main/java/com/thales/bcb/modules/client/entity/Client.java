package com.thales.bcb.modules.client.entity;

import com.thales.bcb.modules.client.enums.DocumentType;
import com.thales.bcb.modules.client.enums.PlanType;
import com.thales.bcb.modules.client.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String documentId;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    private PlanType planType;
    private BigDecimal balance;

    @Column(name = "limit_value")
    private BigDecimal limit;

    private Boolean active;

    @Enumerated(EnumType.STRING)
    private Role role;
}
