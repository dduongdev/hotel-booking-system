package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends BaseAuthException {

    public InvalidOtpException() {
        super(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");
    }
    
}
