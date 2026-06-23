package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class RoomTypeNotBelongToBranchException extends BaseRoomTypeException {

    public RoomTypeNotBelongToBranchException() {
        super(HttpStatus.BAD_REQUEST, "The requested room type is not available in this branch.");
    }
    
}
