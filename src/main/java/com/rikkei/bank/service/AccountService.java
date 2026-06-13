package com.rikkei.bank.service;

import com.rikkei.bank.dto.account.ChangePinRequest;
import com.rikkei.bank.entity.Account;
import com.rikkei.bank.exception.ResourceNotFoundException;
import com.rikkei.bank.exception.UnauthorizedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import com.rikkei.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public BigDecimal getBalance(
            Long accountId
    ) {

        validateAccountId(accountId);

        Account account =
                accountRepository.findById(
                                accountId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Account not found"
                                        )
                        );

        return account.getBalance();
    }
    public void changePin(

            Long accountId,

            ChangePinRequest request
    ) {

        validateAccountId(accountId);
        validateRequest(request);

        Account account =
                accountRepository.findById(
                                accountId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Account not found"
                                        )
                        );

         if(
                 !passwordEncoder.matches(
                         request.getOldPin(),
                         account.getTransactionPin()
                 )
         ) {

             throw new UnauthorizedException(
                     "Old PIN incorrect"
             );
         }

         account.setTransactionPin(
                 passwordEncoder.encode(
                         request.getNewPin()
                 )
         );

        accountRepository.save(
                account
        );
    }

    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new IllegalArgumentException("Account ID must be greater than 0");
        }
    }

    private void validateRequest(ChangePinRequest request) {
        Set<ConstraintViolation<ChangePinRequest>> violations =
                validator.validate(request);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
