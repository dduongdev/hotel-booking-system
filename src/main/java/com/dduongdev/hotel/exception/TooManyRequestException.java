package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class TooManyRequestException extends BaseException {

    public TooManyRequestException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, message);
    }
    
}
