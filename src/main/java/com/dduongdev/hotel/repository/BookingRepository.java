package com.dduongdev.hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dduongdev.hotel.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b WHERE b.room.id = :id AND " + 
        "b.checkOut > :checkIn AND b.checkIn < :checkOut AND " +
        "b.status = 'CONFIRMED'"
    )
    List<Booking> findByRoomIdAndCheckInAndCheckOutOverlap(Integer id, LocalDate checkIn, LocalDate checkOut);
}
