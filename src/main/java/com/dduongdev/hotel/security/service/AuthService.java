package com.dduongdev.hotel.security.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.security.entity.HotelUserDetails;
import com.dduongdev.hotel.security.entity.RefreshToken;
import com.dduongdev.hotel.security.payload.request.LoginRequest;
import com.dduongdev.hotel.security.payload.request.RefreshTokenRequest;
import com.dduongdev.hotel.security.payload.response.LoginResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public LoginResponse authenticate(LoginRequest request) {
        UsernamePasswordAuthenticationToken unauthenticatedToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(unauthenticatedToken);

        HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.create(userDetails.getId());

        return new LoginResponse(jwtToken, refreshToken.getToken());
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedRefreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (storedRefreshToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Refresh token has expired");
        }

        User user = storedRefreshToken.getUser();
        HotelUserDetails userDetails = new HotelUserDetails(user.getId(), user.getUsername(), null, user.getRole());
        String jwtToken = jwtService.generateToken(userDetails);

        RefreshToken newRefreshToken = refreshTokenService.create(user.getId());
        newRefreshToken.setExpirationTime(storedRefreshToken.getExpirationTime());

        refreshTokenService.deleteById(storedRefreshToken.getId());

        return new LoginResponse(jwtToken, newRefreshToken.getToken());
    }

}
