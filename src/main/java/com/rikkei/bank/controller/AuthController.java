package com.rikkei.bank.controller;

import com.rikkei.bank.dto.auth.*;
import com.rikkei.bank.dto.response.ApiResponse;
import com.rikkei.bank.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @ModelAttribute RegisterRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                ApiResponse.<RegisterResponse>builder()
                        .success(true)
                        .message("Registration submitted successfully")
                        .data(
                                authService.register(request)
                        )
                        .build()
                );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody
            LoginRequest request
    ) {

        return ResponseEntity.ok(

                ApiResponse.builder()
                        .success(true)
                        .message("Login successfully")
                        .data(
                                authService.login(request)
                        )
                        .build()
        );
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>>
    refreshToken(

            @RequestBody
            RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.<RefreshTokenResponse>builder()
                        .success(true)
                        .message("Token refreshed successfully")
                        .data(
                                authService.refreshToken(
                                        request
                                )
                        )
                        .build()
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request
    ) {

        authService.logout(
                request
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Logout successfully")
                        .data(null)
                        .build()
        );
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>>
    forgotPassword(

            @RequestBody
            ForgotPasswordRequest request
    ) {

        authService.forgotPassword(
                request
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("OTP sent successfully")
                        .data(null)
                        .build()
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>>
    resetPassword(

            @RequestBody
            ResetPasswordRequest request
    ) {

        authService.resetPassword(
                request
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password reset successfully")
                        .data(null)
                        .build()
        );
    }
}
