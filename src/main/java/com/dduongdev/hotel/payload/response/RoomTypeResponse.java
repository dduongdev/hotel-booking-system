package com.dduongdev.hotel.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RoomTypeResponse {
    private int id;
    private String name;
    private String description;
    private int capacity;
    private double pricePerNight;
    private boolean hidden;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
