package com.dduongdev.hotel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomTypeAvailability {
    private Long id;
    private String name;
    private String description;
    private int capacity;
    private double pricePerNight;
    private int availableRoomCount;
    private String imageUrl;
    private Long branchId;

    public RoomTypeAvailability(Long id, String name, String description, int capacity, double pricePerNight, long availableRoomCount, String imageUrl, Long branchId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.availableRoomCount = (int) availableRoomCount;
        this.imageUrl = imageUrl;
        this.branchId = branchId;
    }
}
