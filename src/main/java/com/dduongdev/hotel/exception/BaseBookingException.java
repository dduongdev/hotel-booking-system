package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatusCode;

public class BaseBookingException extends BaseException {
    public BaseBookingException(HttpStatusCode httpStatusCode, String message) {
        super(httpStatusCode, message);
    }
}
