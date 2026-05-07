package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoomTypeNotFoundException extends RuntimeException {
    public RoomTypeNotFoundException(Integer id) {
        super("Room type with ID " + id + " not found");
    }
}
