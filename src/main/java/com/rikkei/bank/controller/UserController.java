package com.rikkei.bank.controller;

import com.rikkei.bank.dto.response.ApiResponse;
import com.rikkei.bank.dto.common.PageRequestDto;
import com.rikkei.bank.dto.user.CreateUserRequest;
import com.rikkei.bank.dto.user.UpdateUserRequest;
import com.rikkei.bank.dto.user.UserResponseDto;
import com.rikkei.bank.entity.User;
import com.rikkei.bank.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>>
    getAllUsers(

            @ModelAttribute
            PageRequestDto request
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
    public ResponseEntity<ApiResponse<UserResponseDto>>
    getUserById(

            @PathVariable
            Long id
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
    @PostMapping
    public ResponseEntity<ApiResponse<User>>
    createUser(

            @Valid
            @RequestBody
            CreateUserRequest request
    ) {

        User user =
                userService.createUser(
                        request
                );

        return ResponseEntity.status(
                        HttpStatus.CREATED
                )
                .body(
                        ApiResponse.<User>builder()
                                .success(true)
                                .message(
                                        "Created successfully"
                                )
                                .data(user)
                                .build()
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(

            @PathVariable
            Long id,

            @Valid
            @RequestBody
            UpdateUserRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.<User>builder()
                        .success(true)
                        .message("User updated successfully")
                        .data(
                                userService.updateUser(
                                        id,
                                        request
                                )
                        )
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(

            @PathVariable
            Long id
    ) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("User deleted successfully")
                        .data(null)
                        .build()
        );
    }
}
