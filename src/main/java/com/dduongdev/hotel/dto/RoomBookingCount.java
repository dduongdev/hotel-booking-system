package com.dduongdev.hotel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomBookingCount {
    private int id;
    private long bookingCount;

    public RoomBookingCount(int id, long bookingCount) {
        this.id = id;
        this.bookingCount = bookingCount;
    }
}
