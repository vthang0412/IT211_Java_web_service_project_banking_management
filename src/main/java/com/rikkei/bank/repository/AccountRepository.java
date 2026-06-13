package com.rikkei.bank.repository;

import com.rikkei.bank.entity.Account;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface AccountRepository
        extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(
            String accountNumber
    );
    @Lock(
            LockModeType.PESSIMISTIC_WRITE
    )
    @Query("""
            SELECT a
            FROM Account a
            WHERE a.id = :id
            """)
    Optional<Account> findByIdForUpdate(
            @Param("id")
            Long id
    );
}