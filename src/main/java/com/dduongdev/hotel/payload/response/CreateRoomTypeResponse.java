package com.dduongdev.hotel.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class CreateRoomTypeResponse {
    private Long id;
    private String name;
    private String description;
    private int capacity;
    private double pricePerNight;
    private String imageUrl;
    private Long branchId;
    private LocalDateTime createdAt;
}
