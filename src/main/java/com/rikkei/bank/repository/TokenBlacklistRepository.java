package com.rikkei.bank.repository;

import com.rikkei.bank.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository
        extends JpaRepository<TokenBlacklist, Long> {

    boolean existsByAccessToken(
            String accessToken
    );

}