package com.dduongdev.hotel.security.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.repository.UserRepository;
import com.dduongdev.hotel.security.entity.RefreshToken;
import com.dduongdev.hotel.security.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    @Value("${refresh-token.expiration-time}")
    private Long expirationTime;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom;
    private final Base64.Encoder base64Encoder;

    public RefreshToken create(int userId) {
        RefreshToken refreshToken = new RefreshToken();
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        User userProxy = userRepository.getReferenceById(userId);

        refreshToken.setUser(userProxy);
        refreshToken.setExpirationTime(LocalDateTime.now().plusSeconds(expirationTime / 1000));
        refreshToken.setToken(generate());

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteById(int id) {
        refreshTokenRepository.deleteById(id);
    }

    private String generate() {
        byte[] randomBytes = new byte[24]; 
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}