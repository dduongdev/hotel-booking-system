package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class RoomAlreadyBookedException extends BaseRoomException {

    public RoomAlreadyBookedException(Long roomId, String checkIn, String checkOut) {
        super(HttpStatus.BAD_REQUEST, "Room with id " + roomId + " is already booked for the period from " + checkIn + " to " + checkOut);
    }
}
