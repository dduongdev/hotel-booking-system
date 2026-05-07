package com.dduongdev.hotel.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class RoomResponse {
    private int id;
    private String name;
    private boolean hidden;
    private RoomTypeResponse roomType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
