package com.rikkei.bank.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegisterResponse {

    private Long userId;

    private String username;

    private String email;

    private Long kycId;

    private String kycStatus;

    private String message;
}
