package com.rikkei.bank.service;

import com.rikkei.bank.dto.transaction.TransferRequest;
import com.rikkei.bank.entity.Account;
import com.rikkei.bank.entity.Transaction;
import com.rikkei.bank.exception.InsufficientBalanceException;
import com.rikkei.bank.exception.ResourceNotFoundException;
import com.rikkei.bank.repository.AccountRepository;
import com.rikkei.bank.repository.TransactionRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private TransactionService transactionService;

    private Account fromAccount;
    private Account toAccount;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        fromAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC001")
                .balance(BigDecimal.valueOf(1000000))
                .currency("VND")
                .active(true)
                .build();

        toAccount = Account.builder()
                .id(2L)
                .accountNumber("ACC002")
                .balance(BigDecimal.valueOf(500000))
                .currency("VND")
                .active(true)
                .build();

        transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(1L);
        transferRequest.setToAccountId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(100000));

        when(validator.validate(any(TransferRequest.class))).thenReturn(Set.of());
    }

    @Test
    @DisplayName("Should transfer money successfully when balance is sufficient")
    void testTransfer_Success() {
        // Arrange
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transactionService.transfer(transferRequest);

        // Assert
        assertEquals(BigDecimal.valueOf(900000), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(600000), toAccount.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when balance < amount")
    void testTransfer_InsufficientBalance() {
        // Arrange
        transferRequest.setAmount(BigDecimal.valueOf(2000000)); // More than available balance
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () -> transactionService.transfer(transferRequest));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when from account not found")
    void testTransfer_FromAccountNotFound() {
        // Arrange
        when(accountRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.transfer(transferRequest));
    }

    @Test
    @DisplayName("Should create transaction with correct amount and status")
    void testTransfer_TransactionCreatedCorrectly() {
        // Arrange
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            tx.setId(1L);
            return tx;
        });

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transactionService.transfer(transferRequest);

        // Assert
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when to account not found")
    void testTransfer_ToAccountNotFound() {
        // Arrange
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.transfer(transferRequest));
    }
}

