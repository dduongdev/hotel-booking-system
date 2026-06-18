package com.dduongdev.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.UserRegisterRequest;
import com.dduongdev.hotel.payload.request.VerifyOtpRequest;
import com.dduongdev.hotel.payload.response.ApiResponse;
import com.dduongdev.hotel.security.entity.HotelUserDetails;
import com.dduongdev.hotel.security.payload.response.LoginResponse;
import com.dduongdev.hotel.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody UserRegisterRequest request) {
        LoginResponse response = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User register successfully", response));
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyPhone(
            @Valid @RequestBody VerifyOtpRequest request,
            @AuthenticationPrincipal HotelUserDetails principal) {
        LoginResponse response = userService.activateUserByOtp(principal.getId(), request.getOtpCode());
        return ResponseEntity.ok(ApiResponse.success("Phone verified successfully", response));
    }
}
