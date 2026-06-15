package com.dduongdev.hotel.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.ChangePasswordRequest;
import com.dduongdev.hotel.payload.response.ApiResponse;
import com.dduongdev.hotel.security.entity.HotelUserDetails;
import com.dduongdev.hotel.security.payload.request.LoginRequest;
import com.dduongdev.hotel.security.payload.request.RefreshTokenRequest;
import com.dduongdev.hotel.security.payload.response.LoginResponse;
import com.dduongdev.hotel.security.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successfully", response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<LoginResponse>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal HotelUserDetails userDetails) {
        LoginResponse response = authService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", response));
    }
}
