package com.rikkei.bank.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePinRequest {

    @NotBlank(message = "Old PIN is required")
    @Pattern(regexp = "^\\d{4,6}$", message = "Old PIN must contain 4 to 6 digits")
    private String oldPin;

    @NotBlank(message = "New PIN is required")
    @Pattern(regexp = "^\\d{4,6}$", message = "New PIN must contain 4 to 6 digits")
    private String newPin;

}
