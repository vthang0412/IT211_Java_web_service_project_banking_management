package com.rikkei.bank.controller;

import com.rikkei.bank.dto.response.ApiResponse;
import com.rikkei.bank.dto.transaction.TransactionHistoryRequest;
import com.rikkei.bank.dto.transaction.*;
import com.rikkei.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        "/api/v1/customer/transactions"
)
public class TransactionController {

    private final TransactionService
            transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Void>> transfer(

            @RequestBody
            TransferRequest request
    ) {

        transactionService.transfer(request);

        return ResponseEntity.ok(

                ApiResponse.<Void>builder()
                        .success(true)
                        .message(
                                "Transfer successfully"
                        )
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<StatementResponse>>>
    history(

            @ModelAttribute
            TransactionHistoryRequest request
    ) {

        Pageable pageable =
                PageRequest.of(
                        request.getPage(),
                        request.getSize()
                );

        return ResponseEntity.ok(
                ApiResponse.<Page<StatementResponse>>builder()
                        .success(true)
                        .message("Transaction history retrieved successfully")
                        .data(
                                transactionService.getStatement(
                                        request.getAccountId(),
                                        pageable
                                )
                        )
                        .build()
        );
    }
}
