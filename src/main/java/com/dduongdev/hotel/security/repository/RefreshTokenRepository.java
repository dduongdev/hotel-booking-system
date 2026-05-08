package com.dduongdev.hotel.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dduongdev.hotel.security.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
}
