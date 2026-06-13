package com.rikkei.bank.dto.kyc;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KycApproveRequest {

    @NotNull(message = "Approval status is required")
    private Boolean approved;

}
