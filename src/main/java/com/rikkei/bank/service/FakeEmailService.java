package com.rikkei.bank.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class FakeEmailService implements EmailService {

    @Override
    public void sendOtp(String email, String otp) {
        // Fake implementation for development — just log the OTP instead of sending email
        System.out.println("[FakeEmailService] sending OTP to " + email + " : " + otp);
    }
}

