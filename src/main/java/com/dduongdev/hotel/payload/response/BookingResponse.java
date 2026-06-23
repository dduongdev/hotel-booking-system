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
    private Long id;
    private Booking.Status status;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Long roomTypeId;
    private String roomTypeName;
    private Long roomId;
    private String roomName;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
