package com.dduongdev.hotel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dduongdev.hotel.entity.Room;
import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.exception.RoomNotFoundException;
import com.dduongdev.hotel.mapper.RoomMapper;
import com.dduongdev.hotel.payload.request.ChangeRoomStatusRequest;
import com.dduongdev.hotel.payload.request.CreateRoomRequest;
import com.dduongdev.hotel.payload.request.UpdateRoomRequest;
import com.dduongdev.hotel.payload.response.CreateRoomResponse;
import com.dduongdev.hotel.payload.response.RoomResponse;
import com.dduongdev.hotel.repository.RoomRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
    
    private final RoomRepository roomRepository;
    private final EntityManager entityManager;
    private final RoomMapper roomMapper;

    public CreateRoomResponse create(CreateRoomRequest request) {

        RoomType roomTypeProxy = entityManager.getReference(RoomType.class, request.getRoomTypeId());

        Room room = new Room();
        room.setName(request.getName());
        room.setRoomType(roomTypeProxy);
        
        roomRepository.save(room);

        return roomMapper.toCreateRoomResponse(room);
    }

    public Page<RoomResponse> getAll(Pageable pageable) {
        Page<Room> roomsPage = roomRepository.findAll(pageable);
        return roomsPage.map(roomMapper::toRoomResponse);
    }

    public RoomResponse update(Integer id, UpdateRoomRequest request) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException(id));
        room.setName(request.getName());
        room.setStatus(request.getStatus());

        RoomType roomTypeProxy = entityManager.getReference(RoomType.class, request.getRoomTypeId());
        room.setRoomType(roomTypeProxy);

        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }

    public RoomResponse changeStatus(Integer id, ChangeRoomStatusRequest request) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException(id));
        room.setStatus(request.getStatus());

        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }
}
