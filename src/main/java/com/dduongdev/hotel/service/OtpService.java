package com.dduongdev.hotel.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.repository.OtpVerificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {
    
    @Value("${otp.length:6}")
    private int otpLength;

    @Value("${otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    @Value("${otp.resend-cooldown-seconds:60}")
    private int resendCooldownSeconds;

    @Value("${otp.max-attempts:5}")
    private int maxAttempts;

    private final OtpVerificationRepository otpRepository;
    private final SmsService smsService;
    private final SecureRandom secureRandom;

    @Transactional
    public void sendOtp(String phoneNumber) {

    }
}
