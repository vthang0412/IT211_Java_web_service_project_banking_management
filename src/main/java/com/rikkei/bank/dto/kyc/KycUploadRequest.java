package com.rikkei.bank.dto.kyc;

import com.rikkei.bank.validation.ValidImageFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class KycUploadRequest {

    @NotNull(message = "KYC image is required")
    @ValidImageFile
    private MultipartFile file;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "ID number is required")
    @Size(min = 6, max = 20, message = "ID number must be between 6 and 20 characters")
    private String idNumber;
}
