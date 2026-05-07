package com.dduongdev.hotel.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CancelOwnBookingRequest {
    
    @NotNull(message = "Booking ID is required")
    private int bookingId;
}
