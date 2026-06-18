package com.dduongdev.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.response.ApiResponse;
import com.dduongdev.hotel.security.entity.HotelUserDetails;
import com.dduongdev.hotel.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {
    private final UserService userService;

    @PostMapping("/send-for-activation")
    public ResponseEntity<ApiResponse<Void>> sendForActivation(
            @AuthenticationPrincipal HotelUserDetails principal) {
        userService.sendOtpForActivation(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", null));
    }
}
