package com.dduongdev.hotel.payload.response.v1;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateRoomResponse {
    private int id;
    private String name;
    private RoomTypeResponse roomType;
    private LocalDateTime createdAt;
}
