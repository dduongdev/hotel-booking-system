package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatusCode;

public class BaseAuthException extends BaseException {

    public BaseAuthException(HttpStatusCode httpStatusCode, String message) {
        super(httpStatusCode, message);
    }
    
}
