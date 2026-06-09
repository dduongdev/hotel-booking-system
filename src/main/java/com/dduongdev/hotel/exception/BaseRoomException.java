package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatusCode;

public class BaseRoomException extends BaseException {
    public BaseRoomException(HttpStatusCode httpStatusCode, String message) {
        super(httpStatusCode, message);
    }
}
