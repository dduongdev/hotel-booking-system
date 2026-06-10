package com.dduongdev.hotel.payload.response;

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
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private RoomResponse room;
    private Integer userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
