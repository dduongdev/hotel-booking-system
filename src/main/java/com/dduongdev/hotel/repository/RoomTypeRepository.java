package com.dduongdev.hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.payload.response.RoomTypeAvailabilityResponse;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    Page<RoomType> findAll(Pageable pageable);

    @Query("SELECT new com.dduongdev.hotel.payload.response.RoomTypeAvailabilityResponse(\r\n" + //
                "\trt.id,\r\n" + //
                "\trt.name,\r\n" + //
                "\trt.description,\r\n" + //
                "\trt.capacity,\r\n" + //
                "\trt.pricePerNight,\r\n" + //
                "\tCOUNT(r.id)\r\n" + //
                ")\r\n" + //
                "FROM RoomType rt\r\n" + //
                "LEFT JOIN Room r\r\n" + //
                "ON r.roomType.id = rt.id\r\n" + //
                "WHERE rt.hidden = false AND r.id NOT IN (\r\n" + //
                "\tSELECT sr.id\r\n" + //
                "\tFROM Room sr\r\n" + //
                "\tJOIN Booking b\r\n" + //
                "\tON sr.id = b.room.id\r\n" + //
                "\tWHERE b.checkIn < :checkOut AND b.checkOut > :checkIn\r\n" + //
                "\t\tAND b.status = 'CONFIRMED'\r\n" + //
                "\t)\r\n" + //
                "GROUP BY rt.id, rt.name, rt.description, rt.capacity, rt.pricePerNight")
    List<RoomTypeAvailabilityResponse> findRoomTypeAvailabilityByCheckInAndCheckOut(LocalDate checkIn, LocalDate checkOut);
}
