package com.dduongdev.hotel.payload.response;

import java.time.LocalDateTime;

import com.dduongdev.hotel.entity.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class RoomResponse {
    private int id;
    private String name;
    private Room.Status status;
    private RoomTypeResponse roomType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
