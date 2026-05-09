package com.dduongdev.hotel.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dduongdev.hotel.dto.RoomBookingCount;
import com.dduongdev.hotel.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    Page<Room> findAll(Pageable pageable);
    Optional<Room> findById(Integer id);

    @Query("SELECT r FROM Room r " +
        "JOIN FETCH r.roomType " +
        "WHERE r.id NOT IN ( " +
        "SELECT b.room.id FROM Booking b " +
        "WHERE b.checkOut > :checkIn AND b.checkIn < :checkOut AND " + 
        "b.status = 'CONFIRMED'" +
        ")"
    )
    Page<Room> findAvailableRoomsByCheckInAndCheckOut(LocalDate checkIn, LocalDate checkOut, Pageable pageable);
    
    @Query("SELECT new com.dduongdev.hotel.dto.RoomBookingCount(r.id, COUNT(b.id))\r\n" + //
                "FROM Room r\r\n" + //
                "LEFT JOIN Booking b\r\n" + //
                "ON r.id = b.room.id\r\n" + //
                "WHERE r.roomType.id = :roomTypeId AND r.roomType.hidden = false AND\r\n" + //
                "\tr.id NOT IN (\r\n" + //
                "\t\tSELECT b.room.id\r\n" + //
                "\t\tFROM Booking b\r\n" + //
                "\t\tWHERE b.checkIn < :checkOut AND\r\n" + //
                "\t\t\tb.checkOut > :checkIn AND\r\n" + //
                "\t\t\tb.status = 'CONFIRMED'\r\n" + //
                "\t)\r\n" + //
                "GROUP BY r.id")
    List<RoomBookingCount> countBookingsForAvailableRoomsByRoomType(int roomTypeId, LocalDate checkIn, LocalDate checkOut);
}
