package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.mapper.RoomTypeMapper;
import com.dduongdev.hotel.payload.request.v1.ChangeRoomTypeHiddenStateRequest;
import com.dduongdev.hotel.payload.request.v1.CreateRoomTypeRequest;
import com.dduongdev.hotel.payload.request.v1.UpdateRoomTypeRequest;
import com.dduongdev.hotel.payload.response.v1.CreateRoomTypeResponse;
import com.dduongdev.hotel.payload.response.v1.RoomTypeAvailabilityResponse;
import com.dduongdev.hotel.payload.response.v1.RoomTypeResponse;
import com.dduongdev.hotel.repository.RoomTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;

    public CreateRoomTypeResponse create(CreateRoomTypeRequest request) {
        RoomType roomType = new RoomType();

        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setCapacity(request.getCapacity());
        roomType.setPricePerNight(request.getPricePerNight());

        roomTypeRepository.save(roomType);

        return roomTypeMapper.toCreateRoomTypeResponse(roomType);
    }

    public Page<RoomTypeResponse> getAll(Pageable pageable) {
        Page<RoomType> roomTypesPage = roomTypeRepository.findAll(pageable);
        return roomTypesPage.map(roomTypeMapper::toRoomTypeResponse);
    }

    @Transactional
    public RoomTypeResponse update(Integer id, UpdateRoomTypeRequest request) {
        RoomType storedroomType = roomTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room type with id " + id + " not found"));
        
        storedroomType.setName(request.getName());
        storedroomType.setDescription(request.getDescription());
        storedroomType.setCapacity(request.getCapacity());
        storedroomType.setPricePerNight(request.getPricePerNight());

        roomTypeRepository.save(storedroomType);

        return roomTypeMapper.toRoomTypeResponse(storedroomType);
    }

    @Transactional
    public RoomTypeResponse changeHiddenState(Integer id, ChangeRoomTypeHiddenStateRequest request) {
        RoomType storedroomType = roomTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room type with id " + id + " not found"));
        
        storedroomType.setHidden(request.isHidden());

        roomTypeRepository.save(storedroomType);

        return roomTypeMapper.toRoomTypeResponse(storedroomType);
    }

    public List<RoomTypeAvailabilityResponse> getRoomTypeAvailability(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

         if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        return roomTypeRepository.findRoomTypeAvailabilityByCheckInAndCheckOut(checkIn, checkOut);
    }
}
