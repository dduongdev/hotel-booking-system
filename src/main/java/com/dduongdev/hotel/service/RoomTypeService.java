package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.dto.RoomTypeAvailability;
import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.mapper.RoomTypeMapper;
import com.dduongdev.hotel.payload.request.ChangeRoomTypeHiddenStateRequest;
import com.dduongdev.hotel.payload.request.CreateRoomTypeRequest;
import com.dduongdev.hotel.payload.request.UpdateRoomTypeRequest;
import com.dduongdev.hotel.payload.response.CreateRoomTypeResponse;
import com.dduongdev.hotel.payload.response.RoomTypeAvailabilityResponse;
import com.dduongdev.hotel.payload.response.RoomTypeResponse;
import com.dduongdev.hotel.repository.RoomTypeRepository;
import com.dduongdev.hotel.util.Constants;

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
        Page<RoomType> roomTypes = roomTypeRepository.findAllByOrderByPricePerNightAsc(pageable);
        return roomTypes.map(roomTypeMapper::toRoomTypeResponse);
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

    public List<RoomTypeAvailabilityResponse> getAllRoomTypeAvailability(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

         if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        LocalDateTime checkInTime = LocalDateTime.of(checkIn, Constants.CHECK_IN_TIME);
        LocalDateTime checkOutTime = LocalDateTime.of(checkOut, Constants.CHECK_OUT_TIME);

        List<RoomTypeAvailability> roomTypes = roomTypeRepository.findAllRoomTypeAvailabilityByCheckInAndCheckOut(checkInTime, checkOutTime);
        roomTypes.sort((a, b) -> Double.compare(a.getPricePerNight(), b.getPricePerNight()));
        return roomTypes.stream().map(roomType -> roomTypeMapper.toRoomTypeAvailabilityResponse(roomType)).toList();
    }
}
