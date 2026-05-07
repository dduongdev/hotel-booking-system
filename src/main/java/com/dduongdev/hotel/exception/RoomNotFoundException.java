package com.dduongdev.hotel.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(Integer id) {
        super("Room with ID " + id + " not found");
    }
}
