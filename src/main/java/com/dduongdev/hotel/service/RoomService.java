package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.Room;
import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.mapper.RoomMapper;
import com.dduongdev.hotel.payload.request.CreateRoomRequest;
import com.dduongdev.hotel.payload.request.UpdateRoomRequest;
import com.dduongdev.hotel.payload.response.CreateRoomResponse;
import com.dduongdev.hotel.payload.response.RoomResponse;
import com.dduongdev.hotel.repository.RoomRepository;
import com.dduongdev.hotel.repository.RoomTypeRepository;
import com.dduongdev.hotel.util.Constants;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
    
    private final RoomRepository roomRepository;
    private final EntityManager entityManager;
    private final RoomMapper roomMapper;
    private final RoomTypeRepository roomTypeRepository;

    public CreateRoomResponse create(CreateRoomRequest request) {

        if (!roomTypeRepository.existsById(request.getRoomTypeId())) {
            throw new ResourceNotFoundException("Room type with id " + request.getRoomTypeId() + " not found");
        }

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

    @Transactional
    public RoomResponse update(Integer id, UpdateRoomRequest request) {

        if (!roomTypeRepository.existsById(request.getRoomTypeId())) {
            throw new ResourceNotFoundException("Room type with id " + request.getRoomTypeId() + " not found");
        }

        Room room = roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " not found"));
        room.setName(request.getName());
        room.setHidden(request.isHidden());

        RoomType roomTypeProxy = entityManager.getReference(RoomType.class, request.getRoomTypeId());
        room.setRoomType(roomTypeProxy);

        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }

    public Page<RoomResponse> getAvailableRoomsByCheckInAndCheckOut(LocalDate checkIn, LocalDate checkOut, Pageable pageable) {
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        LocalDateTime checkInTime = LocalDateTime.of(checkIn, Constants.CHECK_IN_TIME);
        LocalDateTime checkOutTime = LocalDateTime.of(checkOut, Constants.CHECK_OUT_TIME);

        Page<Room> roomsPage = roomRepository.findAvailableRoomsByCheckInAndCheckOut(checkInTime, checkOutTime, pageable);
        return roomsPage.map(roomMapper::toRoomResponse);
    }
}
