package com.dduongdev.hotel.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dduongdev.hotel.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findAll(Pageable pageable);

    Optional<Room> findById(Long id);

    @Query("""
            SELECT r
            FROM Room r
            JOIN FETCH r.roomType
            WHERE r.hidden = false
              AND r.id NOT IN (
                SELECT b.room.id
                FROM Booking b
                WHERE (b.status = 'CONFIRMED' OR b.status = 'CHECKED_IN')
                  AND b.checkOut > :checkIn
                  AND b.checkIn < :checkOut
            )
            """)
    Page<Room> findAvailableRoomsByCheckInAndCheckOut(LocalDateTime checkIn, LocalDateTime checkOut, Pageable pageable);

    @Query("""
            SELECT r
            FROM Room r
            LEFT JOIN Booking next_b ON next_b.room.id = r.id
                AND (next_b.status = 'CONFIRMED' OR next_b.status = 'CHECKED_IN')
                AND next_b.checkIn >= :checkOut
            WHERE r.roomType.id = :roomTypeId
                AND r.hidden = false
                AND NOT EXISTS (
                    SELECT b.id
                    FROM Booking b
                    WHERE b.room.id = r.id
                    AND (b.status = 'CONFIRMED' OR b.status = 'CHECKED_IN')
                    AND b.checkIn < :checkOut
                    AND b.checkOut > :checkIn
                )
            GROUP BY r.id
            ORDER BY
                MIN(next_b.checkIn) ASC NULLS LAST,
                r.bookingCount ASC
            """)
    List<Room> findBestFitForBooking(Long roomTypeId, LocalDateTime checkIn, LocalDateTime checkOut);
}
