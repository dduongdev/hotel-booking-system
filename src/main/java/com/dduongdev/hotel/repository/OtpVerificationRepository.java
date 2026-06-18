package com.dduongdev.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dduongdev.hotel.entity.OtpVerification;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long>  {
    Optional<OtpVerification> findFirstByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

    Optional<OtpVerification> findFirstByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(String phoneNumber);
}
