package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class InvalidCurrentPasswordException extends BaseAuthException {

    public InvalidCurrentPasswordException() {
        super(HttpStatus.BAD_REQUEST, "Current password is incorrect");
    }
    
}
