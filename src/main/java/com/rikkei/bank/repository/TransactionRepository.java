package com.rikkei.bank.repository;

import com.rikkei.bank.entity.Transaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE
                t.fromAccount.id = :accountId
                OR
                t.toAccount.id = :accountId
            ORDER BY t.createdAt DESC
            """)
    Page<Transaction> findStatement(
            @Param("accountId")
            Long accountId,
            Pageable pageable
    );
}