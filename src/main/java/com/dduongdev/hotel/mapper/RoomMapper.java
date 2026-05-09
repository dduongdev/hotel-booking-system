package com.dduongdev.hotel.mapper;

import org.springframework.stereotype.Component;

import com.dduongdev.hotel.entity.Room;
import com.dduongdev.hotel.payload.response.v1.CreateRoomResponse;
import com.dduongdev.hotel.payload.response.v1.RoomResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoomMapper {
    
    private final RoomTypeMapper roomTypeMapper;

    public RoomResponse toRoomResponse(Room room) {
        return new RoomResponse(
            room.getId(),
            room.getName(),
            room.isHidden(),
            roomTypeMapper.toRoomTypeResponse(room.getRoomType()),
            room.getCreatedAt(),
            room.getUpdatedAt()
        );
    }

    public CreateRoomResponse toCreateRoomResponse(Room room) {
        return new CreateRoomResponse(
            room.getId(),
            room.getName(),
            roomTypeMapper.toRoomTypeResponse(room.getRoomType()),
            room.getCreatedAt()
        );
    }
}
