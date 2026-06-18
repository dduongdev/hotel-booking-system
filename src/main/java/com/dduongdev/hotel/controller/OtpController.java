package com.dduongdev.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.SendOtpRequest;
import com.dduongdev.hotel.payload.response.ApiResponse;
import com.dduongdev.hotel.service.OtpService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    @RequestMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        otpService.sendOtp(request.getPhoneNumber());
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", null));
    }
}
