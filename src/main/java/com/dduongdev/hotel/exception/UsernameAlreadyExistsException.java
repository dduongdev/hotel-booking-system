package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BaseException {
    public UsernameAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Username already exists");
    }
}
