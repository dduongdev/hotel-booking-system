package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class RoomTypeNotAvailableException extends BaseBookingException {
    public RoomTypeNotAvailableException(Integer roomTypeId, String checkIn, String checkOut) {
        super(HttpStatus.BAD_REQUEST, "Room type with id " + roomTypeId + " has no available rooms for the period from " + checkIn + " to " + checkOut);
    }
}
