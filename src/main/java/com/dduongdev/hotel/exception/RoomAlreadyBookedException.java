package com.dduongdev.hotel.exception;

public class RoomAlreadyBookedException extends RuntimeException {

    public RoomAlreadyBookedException(Integer roomId, String checkIn, String checkOut) {
        super("Room with id " + roomId + " is already booked for the period from " + checkIn + " to " + checkOut);
    }
    
}
