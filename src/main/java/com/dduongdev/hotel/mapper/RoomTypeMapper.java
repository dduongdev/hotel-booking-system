package com.dduongdev.hotel.mapper;

import org.springframework.stereotype.Component;

import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.dto.RoomTypeAvailability;
import com.dduongdev.hotel.payload.response.CreateRoomTypeResponse;
import com.dduongdev.hotel.payload.response.RoomTypeAvailabilityResponse;
import com.dduongdev.hotel.payload.response.RoomTypeResponse;

@Component
public class RoomTypeMapper {
    public RoomTypeResponse toRoomTypeResponse(RoomType roomType) {
        return new RoomTypeResponse(
                roomType.getId(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getCapacity(),
                roomType.getPricePerNight(),
                roomType.isHidden(),
                roomType.getImageUrl(),
                roomType.getBranch() != null ? roomType.getBranch().getId() : null,
                roomType.getCreatedAt(),
                roomType.getUpdatedAt()
        );
    }

    public RoomTypeAvailabilityResponse toRoomTypeAvailabilityResponse(RoomTypeAvailability availability) {
        return new RoomTypeAvailabilityResponse(
                availability.getId(),
                availability.getName(),
                availability.getDescription(),
                availability.getCapacity(),
                availability.getPricePerNight(),
                availability.getAvailableRoomCount(),
                availability.getImageUrl(),
                availability.getBranchId()
        );
    }

    public CreateRoomTypeResponse toCreateRoomTypeResponse(RoomType roomType) {
        return new CreateRoomTypeResponse(
                roomType.getId(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getCapacity(),
                roomType.getPricePerNight(),
                roomType.getImageUrl(),
                roomType.getBranch() != null ? roomType.getBranch().getId() : null,
                roomType.getCreatedAt()
        );
    }
}
