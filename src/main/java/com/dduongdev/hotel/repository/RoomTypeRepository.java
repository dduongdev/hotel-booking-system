package com.dduongdev.hotel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dduongdev.hotel.dto.RoomTypeAvailability;
import com.dduongdev.hotel.entity.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    Page<RoomType> findAllByOrderByPricePerNightAsc(Pageable pageable);

    @Query("""
            SELECT new com.dduongdev.hotel.dto.RoomTypeAvailability(
                rt.id,
                rt.name,
                rt.description,
                rt.capacity,
                rt.pricePerNight,
                COUNT(DISTINCT r.id) - (
                    SELECT COUNT(b.id)
                    FROM Booking b
                    WHERE b.roomType.id = rt.id
                        AND (b.status = 'CONFIRMED' OR b.status = 'CHECKED_IN')
                        AND b.checkIn < :checkOut
                        AND b.checkOut > :checkIn
                )
            )
            FROM RoomType rt
            LEFT JOIN Room r ON r.roomType.id = rt.id AND r.hidden = false
            WHERE rt.hidden = false
            GROUP BY rt.id, rt.name, rt.description, rt.capacity, rt.pricePerNight
            """)
    List<RoomTypeAvailability> findAllRoomTypeAvailabilityByCheckInAndCheckOut(LocalDateTime checkIn,
            LocalDateTime checkOut);

    @Query("""
            SELECT new com.dduongdev.hotel.dto.RoomTypeAvailability(
                rt.id,
                rt.name,
                rt.description,
                rt.capacity,
                rt.pricePerNight,
                COUNT(DISTINCT r.id) - (
                    SELECT COUNT(b.id)
                    FROM Booking b
                    WHERE b.roomType.id = rt.id
                        AND (b.status = 'CONFIRMED' OR b.status = 'CHECKED_IN')
                        AND b.checkIn < :checkOut
                        AND b.checkOut > :checkIn
                )
            )
            FROM RoomType rt
            LEFT JOIN Room r ON r.roomType.id = rt.id AND r.hidden = false
            WHERE rt.id = :id
              AND rt.hidden = false
            GROUP BY rt.id, rt.name, rt.description, rt.capacity, rt.pricePerNight
            """)
    RoomTypeAvailability findRoomTypeAvailabilityByIdAndCheckInAndCheckOut(int id, LocalDateTime checkIn, LocalDateTime checkOut);
}
