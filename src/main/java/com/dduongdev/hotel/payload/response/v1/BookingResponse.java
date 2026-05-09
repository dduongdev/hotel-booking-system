package com.dduongdev.hotel.payload.response.v1;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dduongdev.hotel.entity.Booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BookingResponse {
    private int id;
    private Booking.Status status;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private RoomResponse room;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
