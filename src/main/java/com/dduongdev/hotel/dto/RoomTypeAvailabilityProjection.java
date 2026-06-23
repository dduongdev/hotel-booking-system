package com.dduongdev.hotel.dto;

public interface RoomTypeAvailabilityProjection {
    Long getId();
    String getName();
    String getDescription();
    Integer getCapacity();
    Double getPricePerNight();
    Long getAvailableRooms();
    String getImageUrl();
    Long getBranchId();
}
