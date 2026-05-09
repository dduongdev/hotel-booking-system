package com.dduongdev.hotel.mapper;

import org.springframework.stereotype.Component;

import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.payload.response.CreateRoomTypeResponse;
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
                roomType.getCreatedAt(),
                roomType.getUpdatedAt()
        );
    }

    public CreateRoomTypeResponse toCreateRoomTypeResponse(RoomType roomType) {
        return new CreateRoomTypeResponse(
                roomType.getId(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getCapacity(),
                roomType.getPricePerNight(),
                roomType.getCreatedAt()
        );
    }
}
