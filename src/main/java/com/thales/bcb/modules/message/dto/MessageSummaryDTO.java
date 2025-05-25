package com.thales.bcb.modules.message.dto;

import com.thales.bcb.modules.message.enums.Priority;
import com.thales.bcb.modules.message.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSummaryDTO {
    private UUID id;
    private Priority priority;
    private BigDecimal cost;
    private String content;
    private Status status;
}
