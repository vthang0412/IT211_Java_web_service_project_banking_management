package com.rikkei.bank.controller;

import com.rikkei.bank.dto.common.PageRequestDto;
import com.rikkei.bank.dto.response.ApiResponse;
import com.rikkei.bank.dto.user.UserResponseDto;
import com.rikkei.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff/users")
@RequiredArgsConstructor
public class StaffUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getAllUsers(
            @ModelAttribute PageRequestDto request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<UserResponseDto>>builder()
                        .success(true)
                        .message("Users retrieved successfully")
                        .data(
                                userService.getAllUsers(
                                        request
                                )
                        )
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.<UserResponseDto>builder()
                        .success(true)
                        .message("User retrieved successfully")
                        .data(
                                userService.getUserById(id)
                        )
                        .build()
        );
    }
}
