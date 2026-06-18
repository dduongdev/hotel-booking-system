package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class PhoneNumberAlreadyExistsException extends BaseException {

    public PhoneNumberAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Phone number already exists");
    }
    
}
