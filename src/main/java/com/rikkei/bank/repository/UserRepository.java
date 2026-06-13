package com.rikkei.bank.repository;

import com.rikkei.bank.dto.user.UserResponseDto;
import com.rikkei.bank.entity.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByUsername(
            String username
    );

    boolean existsByUsername(
            String username
    );

    @Query("""
        SELECT new com.rikkei.bank.dto.user.UserResponseDto(
            u.id,
            u.username,
            u.email,
            u.phoneNumber,
            u.isActive,
            u.isKyc,
            r.name
        )
        FROM User u
        JOIN u.role r
    """)
    Page<UserResponseDto> getAllUsers(
            Pageable pageable
    );
    Optional<User> findByEmail(
            String email
    );

    boolean existsByEmail(
            String email
    );

    boolean existsByPhoneNumber(
            String phoneNumber
    );
}
