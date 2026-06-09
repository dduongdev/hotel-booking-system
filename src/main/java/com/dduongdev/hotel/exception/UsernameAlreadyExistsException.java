package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BaseException {
    public UsernameAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
