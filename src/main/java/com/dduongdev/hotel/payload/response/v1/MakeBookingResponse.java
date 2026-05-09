package com.dduongdev.hotel.payload.response.v1;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MakeBookingResponse {
    private int id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private RoomResponse room;
    private LocalDateTime createdAt;
}
