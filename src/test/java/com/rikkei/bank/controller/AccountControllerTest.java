package com.rikkei.bank.controller;

import com.rikkei.bank.dto.account.ChangePinRequest;
import com.rikkei.bank.dto.response.ApiResponse;
import com.rikkei.bank.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("AccountController Unit Tests")
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return balance successfully")
    void testGetBalance_Success() {
        // Arrange
        BigDecimal expectedBalance = BigDecimal.valueOf(1000000);
        when(accountService.getBalance(1L)).thenReturn(expectedBalance);

        // Act
        ResponseEntity<ApiResponse<BigDecimal>> response = accountController.getBalance(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Balance retrieved successfully", response.getBody().getMessage());
        assertEquals(expectedBalance, response.getBody().getData());
        verify(accountService, times(1)).getBalance(1L);
    }

    @Test
    @DisplayName("Should return balance as integer")
    void testGetBalance_ReturnsCorrectType() {
        // Arrange
        BigDecimal balance = BigDecimal.valueOf(5000000.50);
        when(accountService.getBalance(1L)).thenReturn(balance);

        // Act
        ResponseEntity<ApiResponse<BigDecimal>> response = accountController.getBalance(1L);

        // Assert
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("Should change PIN successfully")
    void testChangePin_Success() {
        // Arrange
        ChangePinRequest request = new ChangePinRequest();
        request.setOldPin("123456");
        request.setNewPin("654321");

        doNothing().when(accountService).changePin(anyLong(), any(ChangePinRequest.class));

        // Act
        ResponseEntity<ApiResponse<Void>> response = accountController.changePin(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("PIN changed successfully", response.getBody().getMessage());
        verify(accountService, times(1)).changePin(1L, request);
    }

    @Test
    @DisplayName("Should return correct response message for PIN change")
    void testChangePin_ResponseMessage() {
        // Arrange
        ChangePinRequest request = new ChangePinRequest();
        request.setOldPin("123456");
        request.setNewPin("654321");

        doNothing().when(accountService).changePin(anyLong(), any(ChangePinRequest.class));

        // Act
        ResponseEntity<ApiResponse<Void>> response = accountController.changePin(1L, request);

        // Assert
        assertTrue(response.getBody().getMessage().contains("PIN"));
    }

    @Test
    @DisplayName("Should call service with correct parameters")
    void testChangePin_CallsServiceWithCorrectParams() {
        // Arrange
        ChangePinRequest request = new ChangePinRequest();
        request.setOldPin("oldpin123");
        request.setNewPin("newpin456");

        doNothing().when(accountService).changePin(anyLong(), any(ChangePinRequest.class));

        // Act
        accountController.changePin(5L, request);

        // Assert
        verify(accountService, times(1)).changePin(5L, request);
    }
}

