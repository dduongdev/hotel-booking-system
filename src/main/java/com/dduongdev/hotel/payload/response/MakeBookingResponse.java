package com.dduongdev.hotel.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MakeBookingResponse {
    private int id;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private RoomResponse room;
    private LocalDateTime createdAt;
}
