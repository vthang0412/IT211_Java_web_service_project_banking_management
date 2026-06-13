package com.rikkei.bank.service;

import com.rikkei.bank.entity.TokenBlacklist;
import com.rikkei.bank.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "token.blacklist.backend", havingValue = "database", matchIfMissing = true)
public class DbTokenBlacklistService implements TokenBlacklistService {

    private final TokenBlacklistRepository blacklistRepository;

    @Override
    public void blacklistToken(String token, long ttlSeconds) {
        TokenBlacklist blacklist = TokenBlacklist.builder()
                .accessToken(token)
                .blacklistedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        blacklistRepository.save(blacklist);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklistRepository.existsByAccessToken(token);
    }
}

