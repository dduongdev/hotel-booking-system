package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.Branch;
import com.dduongdev.hotel.entity.Room;
import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.exception.RoomNotBelongToBranchException;
import com.dduongdev.hotel.exception.RoomTypeNotBelongToBranchException;
import com.dduongdev.hotel.mapper.RoomMapper;
import com.dduongdev.hotel.payload.request.CreateRoomRequest;
import com.dduongdev.hotel.payload.request.UpdateRoomRequest;
import com.dduongdev.hotel.payload.response.CreateRoomResponse;
import com.dduongdev.hotel.payload.response.RoomResponse;
import com.dduongdev.hotel.repository.BranchRepository;
import com.dduongdev.hotel.repository.RoomRepository;
import com.dduongdev.hotel.repository.RoomTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
    
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final RoomTypeRepository roomTypeRepository;
    private final BranchRepository branchRepository;

    @Transactional
    public CreateRoomResponse create(Long branchId, CreateRoomRequest request) {
        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch not found");
        }

        if (!roomTypeRepository.existsById(request.getRoomTypeId())) {
            throw new ResourceNotFoundException("Room type not found");
        }

        if (!roomTypeRepository.existsByIdAndBranchId(request.getRoomTypeId(), branchId)) {
            throw new RoomTypeNotBelongToBranchException();
        }

        Branch branchProxy = branchRepository.getReferenceById(branchId);
        RoomType roomTypeProxy = roomTypeRepository.getReferenceById(request.getRoomTypeId());

        Room room = new Room();
        room.setName(request.getName());
        room.setRoomType(roomTypeProxy);
        room.setBranch(branchProxy);
        
        roomRepository.save(room);

        return roomMapper.toCreateRoomResponse(room);
    }

    @Transactional(readOnly = true)
    public Page<RoomResponse> getAllByBranch(Long branchId, Pageable pageable) {
        Page<Room> roomsPage = roomRepository.findAllByBranchId(branchId, pageable);
        return roomsPage.map(roomMapper::toRoomResponse);
    }

    @Transactional
    public RoomResponse update(Long id, Long branchId, UpdateRoomRequest request) {
        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch not found");
        }

        if (!roomTypeRepository.existsById(request.getRoomTypeId())) {
            throw new ResourceNotFoundException("Room type not found");
        }

        if (!roomTypeRepository.existsByIdAndBranchId(request.getRoomTypeId(), branchId)) {
            throw new RoomTypeNotBelongToBranchException();
        }

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getBranch() == null || !room.getBranch().getId().equals(branchId)) {
            throw new RoomNotBelongToBranchException();
        }

        room.setName(request.getName());
        room.setHidden(Boolean.TRUE.equals(request.getHidden()));

        RoomType roomTypeProxy = roomTypeRepository.getReferenceById(request.getRoomTypeId());
        room.setRoomType(roomTypeProxy);

        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }

    @Transactional(readOnly = true)
    public Page<RoomResponse> getAvailableRoomsByCheckInAndCheckOut(Long branchId, LocalDate checkIn, LocalDate checkOut, Pageable pageable) {
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id " + branchId + " not found"));

        LocalDateTime checkInTime = LocalDateTime.of(checkIn, branch.getCheckInTime());
        LocalDateTime checkOutTime = LocalDateTime.of(checkOut, branch.getCheckOutTime());

        Page<Room> roomsPage = roomRepository.findAvailableRoomsByCheckInAndCheckOut(branchId, checkInTime, checkOutTime, pageable);
        return roomsPage.map(roomMapper::toRoomResponse);
    }
}
