package com.rikkei.bank.service;

import com.rikkei.bank.dto.common.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rikkei.bank.dto.user.CreateUserRequest;
import com.rikkei.bank.dto.user.UpdateUserRequest;
import com.rikkei.bank.dto.user.UserResponseDto;
import com.rikkei.bank.entity.Role;
import com.rikkei.bank.entity.User;
import com.rikkei.bank.exception.ResourceNotFoundException;
import com.rikkei.bank.repository.RoleRepository;
import com.rikkei.bank.repository.UserRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public Page<UserResponseDto> getAllUsers(
            PageRequestDto request
    ) {

        validateRequest(request);

        Pageable pageable =
                PageRequest.of(
                        request.getPage(),
                        request.getSize()
                );

        return userRepository.getAllUsers(
                pageable
        );
    }

    public UserResponseDto getUserById(
            Long id
    ) {

        validateUserId(id);

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.getIsActive())
                .isKyc(user.getIsKyc())
                .roleName(user.getRole().getName())
                .build();
    }

    public User createUser(
            CreateUserRequest request
    ) {

        Role role =
                roleRepository.findById(
                                request.getRoleId()
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Role not found"
                                )
                        );

        User user =
                User.builder()
                        .username(
                                request.getUsername()
                        )
                        .password(
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
                        )
                        .email(
                                request.getEmail()
                        )
                        .phoneNumber(
                                request.getPhoneNumber()
                        )
                        .role(role)
                        .isActive(true)
                        .isKyc(false)
                        .build();

        return userRepository.save(
                user
        );
    }

    public User updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        validateUserId(id);

        User user =
                userRepository.findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found"
                                        )
                        );

        user.setEmail(
                request.getEmail()
        );

        user.setPhoneNumber(
                request.getPhoneNumber()
        );

        user.setIsActive(
                request.getIsActive()
        );

        return userRepository.save(
                user
        );
    }

    public void deleteUser(
            Long id
    ) {

        validateUserId(id);

        User user =
                userRepository.findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found"
                                        )
                        );

        userRepository.delete(user);
    }

    private void validateUserId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
    }

    private void validateRequest(PageRequestDto request) {
        Set<ConstraintViolation<PageRequestDto>> violations =
                validator.validate(request);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
