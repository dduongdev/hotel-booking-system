package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends BaseException {

    public InternalServerErrorException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error has occurred");
    }
    
}
