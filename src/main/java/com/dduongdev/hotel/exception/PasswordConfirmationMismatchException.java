package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class PasswordConfirmationMismatchException extends BaseAuthException {
    public PasswordConfirmationMismatchException() {
        super(HttpStatus.BAD_REQUEST, "New password and confirmation password do not match");
    }
}
