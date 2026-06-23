package com.dduongdev.hotel.exception;

import org.springframework.http.HttpStatus;

public class RoomNotBelongToBranchException extends BaseRoomException {

    public RoomNotBelongToBranchException() {
        super(HttpStatus.BAD_REQUEST, "The requested room is not available in this branch.");
    }
    
}
