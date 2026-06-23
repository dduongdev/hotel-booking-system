package com.dduongdev.hotel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Service
@Profile("prod")
public class TwilioSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(TwilioSmsService.class);

    @Value("${sms.twilio.account-sid}")
    private String accountSid;

    @Value("${sms.twilio.auth-token}")
    private String authToken;

    @Value("${sms.twilio.phone-number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio SMS service initialized for phone number: {}", fromPhoneNumber);
    }

    @Override
    public void sendOtp(String phoneNumber, String otpCode) {
        try {
            String formattedPhone = phoneNumber.startsWith("0")
                    ? "+84" + phoneNumber.substring(1)
                    : phoneNumber.startsWith("+84") ? phoneNumber : "+84" + phoneNumber;

            Message.creator(
                    new PhoneNumber(formattedPhone),
                    new PhoneNumber(fromPhoneNumber),
                    "LuxeStay: Your OTP code is " + otpCode + ". Valid for 5 minutes."
            ).create();

            log.info("OTP sent to {} via Twilio", formattedPhone);
        } catch (Exception e) {
            log.error("Failed to send OTP to {} via Twilio: {}", phoneNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP. Please try again later.", e);
        }
    }
}
