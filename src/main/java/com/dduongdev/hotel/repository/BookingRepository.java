package com.dduongdev.hotel.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dduongdev.hotel.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    Page<Booking> findAll(Pageable pageable);

    Page<Booking> findByBranchId(Long branchId, Pageable pageable);

    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.room.id = :roomId
                AND b.id != :bookingId
                AND (b.status = 'CONFIRMED' OR b.status = 'CHECKED_IN')
                AND b.checkIn < :checkOut
                AND b.checkOut > :checkIn
            """)
    boolean existsByRoomIdAndOverlappingDates(Long roomId, LocalDateTime checkIn, LocalDateTime checkOut, Long bookingId);
}
