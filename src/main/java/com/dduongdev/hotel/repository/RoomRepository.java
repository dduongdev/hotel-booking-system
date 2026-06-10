package com.dduongdev.hotel.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dduongdev.hotel.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    Page<Room> findAll(Pageable pageable);

    Optional<Room> findById(Integer id);

    @Query("""
            SELECT r
            FROM Room r
            JOIN FETCH r.roomType
            WHERE r.id NOT IN (
                SELECT b.room.id
                FROM Booking b
                WHERE b.status = 'CONFIRMED'
                  AND b.checkOut > :checkIn
                  AND b.checkIn < :checkOut
            )
            """)
    Page<Room> findAvailableRoomsByCheckInAndCheckOut(LocalDateTime checkIn, LocalDateTime checkOut, Pageable pageable);

    @Query("""
        SELECT r
        FROM Room r
        WHERE r.roomType.id = :roomTypeId
          AND EXISTS (
              SELECT rt.id
              FROM RoomType rt
              WHERE r.roomType.id = rt.id
                AND rt.hidden = false
          )
          AND NOT EXISTS (
              SELECT b.room.id
              FROM Booking b
              WHERE b.room.id = r.id
                AND b.status = 'CONFIRMED'
                AND b.checkIn < :checkOut
                AND b.checkOut > :checkIn
          )
        ORDER BY r.bookingCount ASC
        LIMIT 1
        """)
    Optional<Room> findTopAvailableByRoomTypeIdAndDateRange(int roomTypeId, LocalDateTime checkIn, LocalDateTime checkOut);
}
