package com.rikkei.bank.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;

    private String username;

    private String email;

    private String phoneNumber;

    private Boolean isActive;

    private Boolean isKyc;

    private String roleName;

}