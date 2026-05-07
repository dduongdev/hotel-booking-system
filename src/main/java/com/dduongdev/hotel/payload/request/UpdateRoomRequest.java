package com.dduongdev.hotel.payload.request;

import lombok.Getter;

@Getter
public class UpdateRoomRequest {
    private String name;
    private boolean hidden;
    private int roomTypeId;
}
