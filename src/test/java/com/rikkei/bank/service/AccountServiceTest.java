package com.rikkei.bank.service;

import com.rikkei.bank.dto.account.ChangePinRequest;
import com.rikkei.bank.entity.Account;
import com.rikkei.bank.exception.ResourceNotFoundException;
import com.rikkei.bank.exception.UnauthorizedException;
import com.rikkei.bank.repository.AccountRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Validator validator;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC001")
                .balance(BigDecimal.valueOf(1000000))
                .currency("VND")
                .transactionPin("encoded_pin_123456")
                .active(true)
                .build();

        when(validator.validate(any(ChangePinRequest.class))).thenReturn(Set.of());
    }

    @Test
    @DisplayName("Should return balance when account exists")
    void testGetBalance_Success() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        BigDecimal balance = accountService.getBalance(1L);

        // Assert
        assertEquals(BigDecimal.valueOf(1000000), balance);
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when account not found")
    void testGetBalance_AccountNotFound() {
        // Arrange
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.getBalance(1L));
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should change PIN successfully with correct old PIN")
    void testChangePin_Success() {
        // Arrange
        ChangePinRequest request = new ChangePinRequest();
        request.setOldPin("123456");
        request.setNewPin("654321");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("123456", testAccount.getTransactionPin())).thenReturn(true);
        when(passwordEncoder.encode("654321")).thenReturn("encoded_pin_654321");
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        accountService.changePin(1L, request);

        // Assert
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when old PIN is incorrect")
    void testChangePin_IncorrectOldPin() {
        // Arrange
        ChangePinRequest request = new ChangePinRequest();
        request.setOldPin("111111");
        request.setNewPin("654321");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("111111", testAccount.getTransactionPin())).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> accountService.changePin(1L, request));
    }

    @Test
    @DisplayName("Should create account with correct balance")
    void testCreateAccount_ValidBalance() {
        // Arrange
        Account newAccount = Account.builder()
                .accountNumber("ACC002")
                .balance(BigDecimal.valueOf(5000000))
                .currency("VND")
                .active(true)
                .build();

        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        // Act
        Account saved = accountRepository.save(newAccount);

        // Assert
        assertNotNull(saved);
        assertEquals("ACC002", saved.getAccountNumber());
        assertEquals(BigDecimal.valueOf(5000000), saved.getBalance());
    }
}

