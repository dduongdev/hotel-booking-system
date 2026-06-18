package com.dduongdev.hotel.service;

public interface SmsService {
    void sendOtp(String phoneNumber, String otpCode);
}
