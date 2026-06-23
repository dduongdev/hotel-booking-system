package com.dduongdev.hotel.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateRoomResponse {
    private Long id;
    private String name;
    private RoomTypeResponse roomType;
    private LocalDateTime createdAt;
}
