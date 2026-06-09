package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private HttpStatusCode httpStatusCode;

    public BaseException(HttpStatusCode httpStatusCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}