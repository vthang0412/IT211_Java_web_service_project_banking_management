package com.rikkei.bank.dto.kyc;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KycUploadResponse {

    private Long id;

    private String fullName;

    private String idNumber;

    private String imageUrl;

    private String status;
}