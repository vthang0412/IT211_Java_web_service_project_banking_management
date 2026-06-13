package com.rikkei.bank.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotNull(message = "From account ID is required")
    @Positive(message = "From account ID must be greater than 0")
    private Long fromAccountId;

    @NotNull(message = "To account ID is required")
    @Positive(message = "To account ID must be greater than 0")
    private Long toAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1", message = "Amount must be greater than or equal to 1")
    private BigDecimal amount;

}
