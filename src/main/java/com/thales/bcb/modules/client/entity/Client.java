package com.thales.bcb.modules.client.entity;

import com.thales.bcb.modules.client.enums.ClientDocumentType;
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
    private ClientDocumentType documentType;

    @Enumerated(EnumType.STRING)
    private PlanType planType;
    private BigDecimal balance;

    @Column(name = "limit_value")
    private BigDecimal limit;

    private Boolean active;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void processPayment(BigDecimal cost){
        BigDecimal available = getAvailableAmount();

        if(available.compareTo(cost) < 0){
            throw new RuntimeException(this.planType == PlanType.PREPAID
            ? "Saldo insuficiente"
            : "Limite insuficiente");
        }

        if(this.planType == PlanType.PREPAID){
            this.balance = this.balance.subtract(cost);
        } else {
            this.limit = this.limit.subtract(cost);
        }
    }

    public BigDecimal getAvailableAmount(){
        return this.planType == PlanType.PREPAID ? balance : limit;
    }
}
