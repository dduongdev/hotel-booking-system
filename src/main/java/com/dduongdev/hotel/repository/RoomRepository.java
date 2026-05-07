package com.dduongdev.hotel.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
