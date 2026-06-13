package com.rikkei.bank.service;

public interface EmailService {

    void sendOtp(
            String email,
            String otp
    );

}