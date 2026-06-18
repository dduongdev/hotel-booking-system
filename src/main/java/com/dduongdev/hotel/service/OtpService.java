package com.dduongdev.hotel.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.OtpVerification;
import com.dduongdev.hotel.exception.InternalServerErrorException;
import com.dduongdev.hotel.exception.TooManyRequestException;
import com.dduongdev.hotel.repository.OtpVerificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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
        Optional<OtpVerification> latestOtpOpt = otpRepository.findFirstByPhoneNumberOrderByCreatedAtDesc(phoneNumber);
        if (latestOtpOpt.isPresent()) {
            OtpVerification latestOtp = latestOtpOpt.get();
            long passSeconds = Duration.between(latestOtp.getCreatedAt(), LocalDateTime.now()).getSeconds();
            if (passSeconds < resendCooldownSeconds) {
                long waitSeconds = resendCooldownSeconds - passSeconds;
                throw new TooManyRequestException(String.format("Please wait %d seconds before requesting a new OTP", waitSeconds));
            }
        }

        String otp = String.format("%0" + otpLength + "d", secureRandom.nextInt((int) Math.pow(10, otpLength)));

        String otpHash = hashOtp(otp);

        OtpVerification verification = new OtpVerification();
        verification.setPhoneNumber(phoneNumber);
        verification.setOtpHash(otpHash);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        otpRepository.save(verification);

        smsService.sendOtp(phoneNumber, otp);
    }

    @Transactional
    public boolean verifyOtp(String phoneNumber, String otpCode) {
        Optional<OtpVerification> latestOtpOpt = otpRepository.findFirstByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(phoneNumber);

        if (latestOtpOpt.isEmpty()) {
            return false;
        }

        OtpVerification latestOtp = latestOtpOpt.get();
        if (latestOtp.getAttemptCount() >= maxAttempts) {
            throw new TooManyRequestException("Too many failed attempts. Please request a new OTP.");
        }

        latestOtp.setAttemptCount(latestOtp.getAttemptCount() + 1);
        otpRepository.save(latestOtp);

        if (latestOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        String otpHash = hashOtp(otpCode);
        if (otpHash.equals(latestOtp.getOtpHash())) {
            latestOtp.setVerified(true);
            otpRepository.save(latestOtp);
            return true;
        }

        return false;
    }

    private String hashOtp(String otp) {
        if (otp == null) {
            throw new IllegalArgumentException("OTP cannot be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 is missing from JVM environment", e);
            throw new InternalServerErrorException();
        }
    }
}
