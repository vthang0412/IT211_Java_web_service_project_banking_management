package com.rikkei.bank.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @Email(message = "Email must be a valid email address")
    private String email;

    @Pattern(
            regexp = "^\\+?[0-9]{9,15}$",
            message = "Phone number must contain 9 to 15 digits and may start with +"
    )
    private String phoneNumber;

    private Boolean isActive;

}
