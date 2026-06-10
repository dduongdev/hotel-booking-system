package com.dduongdev.hotel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.payload.response.RoomTypeAvailabilityResponse;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    Page<RoomType> findAllByOrderByPricePerNightAsc(Pageable pageable);

    @Query("""
            SELECT new com.dduongdev.hotel.payload.response.RoomTypeAvailabilityResponse(
                rt.id,
                rt.name,
                rt.description,
                rt.capacity,
                rt.pricePerNight,
                COUNT(r.id)
            )
            FROM RoomType rt
            LEFT JOIN Room r ON r.roomType.id = rt.id
            WHERE rt.hidden = false
              AND NOT EXISTS (
                  SELECT b.id
                  FROM Booking b
                  WHERE b.room.id = r.id
                    AND b.status = 'CONFIRMED'
                    AND b.checkIn < :checkOut
                    AND b.checkOut > :checkIn
              )
            GROUP BY rt.id, rt.name, rt.description, rt.capacity, rt.pricePerNight
            """)
    List<RoomTypeAvailabilityResponse> findRoomTypeAvailabilityByCheckInAndCheckOut(LocalDateTime checkIn,
            LocalDateTime checkOut);
}
