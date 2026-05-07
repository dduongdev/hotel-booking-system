package com.dduongdev.hotel.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dduongdev.hotel.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT COUNT(b) > 0 " +
        "FROM Booking b WHERE b.room.id = :id AND " + 
        "b.checkOut > :checkIn AND b.checkIn < :checkOut AND " +
        "b.status = 'CONFIRMED'"
    )
    boolean existsByRoomIdAndCheckInAndCheckOutOverlap(Integer id, LocalDate checkIn, LocalDate checkOut);

    Page<Booking> findByUserId(Integer userId, Pageable pageable);

    Optional<Booking> findByIdAndUserId(Integer id, Integer userId);

    Page<Booking> findAll(Pageable pageable);
}
