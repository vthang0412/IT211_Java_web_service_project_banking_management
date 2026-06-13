package com.rikkei.bank.controller;

import com.rikkei.bank.dto.account.ChangePinRequest;
import com.rikkei.bank.dto.response.ApiResponse;
import com.rikkei.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<BigDecimal>>
    getBalance(

            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.<BigDecimal>builder()
                        .success(true)
                        .message("Balance retrieved successfully")
                        .data(
                                accountService.getBalance(id)
                        )
                        .build()
        );
    }
    @PutMapping("/{id}/change-pin")
    public ResponseEntity<ApiResponse<Void>>
    changePin(

            @PathVariable Long id,

            @RequestBody
            ChangePinRequest request
    ) {

        accountService.changePin(
                id,
                request
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("PIN changed successfully")
                        .data(null)
                        .build()
        );
    }
}
