package com.rikkei.bank.dto.transaction;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class StatementResponse {

    private String transactionCode;

    private BigDecimal amount;

    private String type;

    private String description;

    private LocalDateTime createdAt;

}