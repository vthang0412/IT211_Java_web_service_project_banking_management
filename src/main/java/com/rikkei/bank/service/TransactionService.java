package com.rikkei.bank.service;

import com.rikkei.bank.aspect.AuditTransfer;
import com.rikkei.bank.dto.transaction.*;
import com.rikkei.bank.entity.*;
import com.rikkei.bank.exception.*;
import com.rikkei.bank.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final Validator validator;

    @Transactional
    @AuditTransfer
    public void transfer(
            TransferRequest request
    ) {

        validateRequest(request);

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new IllegalArgumentException("Source account and target account must be different");
        }

        Account fromAccount =
                accountRepository
                        .findByIdForUpdate(
                                request.getFromAccountId()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Source account not found"
                                        )
                        );

        Account toAccount =
                accountRepository
                        .findByIdForUpdate(
                                request.getToAccountId()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Target account not found"
                                        )
                        );

        if (
                fromAccount.getBalance()
                        .compareTo(
                                request.getAmount()
                        ) < 0
        ) {

            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }

        fromAccount.setBalance(
                fromAccount.getBalance()
                        .subtract(
                                request.getAmount()
                        )
        );

        toAccount.setBalance(
                toAccount.getBalance()
                        .add(
                                request.getAmount()
                        )
        );

        Transaction transaction =
                Transaction.builder()
                        .transactionCode(
                                UUID.randomUUID()
                                        .toString()
                        )
                        .amount(
                                request.getAmount()
                        )
                        .status("SUCCESS")
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .fromAccount(fromAccount)
                        .toAccount(toAccount)
                        .build();

        transactionRepository.save(
                transaction
        );

        accountRepository.save(
                fromAccount
        );

                accountRepository.save(
                toAccount
        );
    }

    public Page<StatementResponse>
    getStatement(

            Long accountId,

            Pageable pageable
    ) {

        return transactionRepository
                .findStatement(
                        accountId,
                        pageable
                )
                .map(transaction -> {

                    String type;

                    if (
                            transaction
                                    .getFromAccount()
                                    .getId()
                                    .equals(accountId)
                    ) {

                        type = "DEBIT";

                    } else {

                        type = "CREDIT";
                    }

                    return StatementResponse
                            .builder()
                            .transactionCode(
                                    transaction.getTransactionCode()
                            )
                            .amount(
                                    transaction.getAmount()
                            )
                            .description(
                                    transaction.getDescription()
                            )
                            .type(type)
                            .createdAt(
                                    transaction.getCreatedAt()
                            )
                            .build();
                });
    }

    private void validateRequest(TransferRequest request) {
        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
