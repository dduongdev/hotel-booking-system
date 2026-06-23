package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatusCode;

public class BaseRoomTypeException extends BaseException {

    public BaseRoomTypeException(HttpStatusCode httpStatusCode, String message) {
        super(httpStatusCode, message);
    }
    
}
