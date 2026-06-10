package com.dduongdev.hotel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dduongdev.hotel.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Page<Booking> findByUserId(Integer userId, Pageable pageable);

    Optional<Booking> findByIdAndUserId(Integer id, Integer userId);

    Page<Booking> findAll(Pageable pageable);
}
