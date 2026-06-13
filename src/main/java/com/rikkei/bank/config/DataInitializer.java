package com.rikkei.bank.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rikkei.bank.entity.Account;
import com.rikkei.bank.entity.Role;
import com.rikkei.bank.entity.User;
import com.rikkei.bank.repository.AccountRepository;
import com.rikkei.bank.repository.RoleRepository;
import com.rikkei.bank.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer
        implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        seedRoles();

        seedUsersAndAccounts();
    }

    private void seedRoles() {

        if (roleRepository.count() > 0) {
            return;
        }

        roleRepository.save(
                Role.builder()
                        .name("ADMIN")
                        .description(
                                "System Administrator"
                        )
                        .build()
        );

        roleRepository.save(
                Role.builder()
                        .name("STAFF")
                        .description(
                                "Bank Staff"
                        )
                        .build()
        );

        roleRepository.save(
                Role.builder()
                        .name("CUSTOMER")
                        .description(
                                "Bank Customer"
                        )
                        .build()
        );
    }

    private void seedUsersAndAccounts() {

                // Do not bail out early; insert missing users/accounts idempotently

        Role adminRole =
                roleRepository
                        .findByName("ADMIN")
                        .orElseThrow();

        Role staffRole =
                roleRepository
                        .findByName("STAFF")
                        .orElseThrow();

        Role customerRole =
                roleRepository
                        .findByName("CUSTOMER")
                        .orElseThrow();

        User admin = null;
        if (!userRepository.existsByUsername("admin") && userRepository.findByEmail("admin@rikkei.com").isEmpty()) {
            admin = userRepository.save(
                    User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("123456"))
                            .email("admin@rikkei.com")
                            .phoneNumber("0123456789")
                            .isActive(true)
                            .isKyc(true)
                            .role(adminRole)
                            .build()
            );
        } else {
            admin = userRepository.findByUsername("admin").orElseGet(() -> userRepository.findByEmail("admin@rikkei.com").orElse(null));
        }

        User staff = null;
        if (!userRepository.existsByUsername("staff") && userRepository.findByEmail("staff@rikkei.com").isEmpty()) {
            staff = userRepository.save(
                    User.builder()
                            .username("staff")
                            .password(passwordEncoder.encode("123456"))
                            .email("staff@rikkei.com")
                            .phoneNumber("0988888888")
                            .isActive(true)
                            .isKyc(true)
                            .role(staffRole)
                            .build()
            );
        } else {
            staff = userRepository.findByUsername("staff").orElseGet(() -> userRepository.findByEmail("staff@rikkei.com").orElse(null));
        }

        User customer = null;
        if (!userRepository.existsByUsername("customer") && userRepository.findByEmail("customer@rikkei.com").isEmpty()) {
            customer = userRepository.save(
                    User.builder()
                            .username("customer")
                            .password(passwordEncoder.encode("123456"))
                            .email("customer@rikkei.com")
                            .phoneNumber("0999999999")
                            .isActive(true)
                            .isKyc(true)
                            .role(customerRole)
                            .build()
            );
        } else {
            customer = userRepository.findByUsername("customer").orElseGet(() -> userRepository.findByEmail("customer@rikkei.com").orElse(null));
        }

         if (customer != null) {
             if (accountRepository.findByAccountNumber("ACC001").isEmpty()) {
                 accountRepository.save(
                         Account.builder()
                                 .accountNumber("ACC001")
                                 .balance(BigDecimal.valueOf(10000000))
                                 .currency("VND")
                                 .transactionPin(passwordEncoder.encode("123456"))
                                 .active(true)
                                 .user(customer)
                                 .build()
                 );
             }

             if (accountRepository.findByAccountNumber("ACC002").isEmpty()) {
                 accountRepository.save(
                         Account.builder()
                                 .accountNumber("ACC002")
                                 .balance(BigDecimal.valueOf(5000000))
                                 .currency("VND")
                                 .transactionPin(passwordEncoder.encode("123456"))
                                 .active(true)
                                 .user(customer)
                                 .build()
                 );
             }
         }
    }
}