package com.dduongdev.hotel.payload.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MakeBookingRequest {
    
    @NotNull(message = "Room ID is required")
    private int roomId;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;
}
