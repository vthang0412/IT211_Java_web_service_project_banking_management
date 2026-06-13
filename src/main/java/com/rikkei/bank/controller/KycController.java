package com.rikkei.bank.controller;

import com.rikkei.bank.dto.kyc.KycUploadRequest;
import com.rikkei.bank.dto.kyc.KycUploadResponse;
import com.rikkei.bank.dto.response.ApiResponse;
import com.rikkei.bank.service.KycService;
import com.rikkei.bank.security.UserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kyc")
public class KycController {

    private final KycService kycService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<KycUploadResponse>> upload(

            @AuthenticationPrincipal UserPrincipal principal,

            @ModelAttribute
            KycUploadRequest request
    ) {

        Long userId = principal.getId();

        return ResponseEntity.ok(
                ApiResponse.<KycUploadResponse>builder()
                        .success(true)
                        .message("KYC uploaded successfully")
                        .data(
                                kycService.uploadKyc(
                                        userId,
                                        request.getFile(),
                                        request.getFullName(),
                                        request.getIdNumber()
                                )
                        )
                        .build()
        );
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Void>> approve(
            @PathVariable Long id
    ) {

                kycService.approveKyc(id);

                return ResponseEntity.ok(
                        ApiResponse.<Void>builder()
                                .success(true)
                                .message("KYC approved successfully")
                                .data(null)
                                .build()
                );
        }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long id
    ) {

                kycService.rejectKyc(id);

                return ResponseEntity.ok(
                        ApiResponse.<Void>builder()
                                .success(true)
                                .message("KYC rejected successfully")
                                .data(null)
                                .build()
                );
        }
}
