package com.dduongdev.hotel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class ConsoleSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleSmsService.class);

    @Override
    public void sendOtp(String phoneNumber, String otpCode) {
        log.info("========================================");
        log.info("📱 SMS TO: {}", phoneNumber);
        log.info("📝 YOUR OTP CODE: {}", otpCode);
        log.info("⏱  Valid for 5 minutes");
        log.info("========================================");
    }
    
}
