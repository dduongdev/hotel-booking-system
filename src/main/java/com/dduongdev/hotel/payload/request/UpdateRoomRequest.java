package com.dduongdev.hotel.payload.request;

import com.dduongdev.hotel.entity.Room;

import lombok.Getter;

@Getter
public class UpdateRoomRequest {
    private String name;
    private Room.Status status;
    private int roomTypeId;
}
