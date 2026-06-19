package com.dduongdev.hotel.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomTypeAvailabilityResponse {
    private int id;
    private String name;
    private String description;
    private int capacity;
    private double pricePerNight;
    private int availableRoomCount;
    private String imageUrl;

    public RoomTypeAvailabilityResponse(int id, String name, String description, int capacity, double pricePerNight, long availableRoomCount, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.availableRoomCount = (int) availableRoomCount;
        this.imageUrl = imageUrl;
    }
}
