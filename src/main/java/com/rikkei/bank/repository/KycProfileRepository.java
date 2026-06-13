package com.rikkei.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rikkei.bank.entity.KycProfile;

public interface KycProfileRepository
        extends JpaRepository<KycProfile, Long> {
        java.util.Optional<KycProfile> findByUser(com.rikkei.bank.entity.User user);
}