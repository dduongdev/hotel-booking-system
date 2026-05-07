package com.dduongdev.hotel.payload.request;

import com.dduongdev.hotel.entity.Room;

import lombok.Getter;

@Getter
public class ChangeRoomStatusRequest {
    private Room.Status status;
}
