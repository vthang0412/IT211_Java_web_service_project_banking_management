package com.rikkei.bank.service;

public interface TokenBlacklistService {

    void blacklistToken(String token, long ttlSeconds);

    boolean isBlacklisted(String token);
}

