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

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;
    private final StorageService storageService;

    public CreateRoomTypeResponse create(CreateRoomTypeRequest request, MultipartFile image) {
        RoomType roomType = new RoomType();

        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setCapacity(request.getCapacity());
        roomType.setPricePerNight(request.getPricePerNight());

        if (image != null && !image.isEmpty()) {
            String filename = storageService.generateFilename(image.getOriginalFilename());
            String imageUrl = storageService.upload(image, filename);
            roomType.setImageUrl(imageUrl);
        }

        roomTypeRepository.save(roomType);

        return roomTypeMapper.toCreateRoomTypeResponse(roomType);
    }

    public Page<RoomTypeResponse> getAll(Pageable pageable) {
        Page<RoomType> roomTypes = roomTypeRepository.findAllByOrderByPricePerNightDesc(pageable);
        return roomTypes.map(roomTypeMapper::toRoomTypeResponse);
    }

    @Transactional
    public RoomTypeResponse update(Integer id, UpdateRoomTypeRequest request, MultipartFile image) {
        RoomType storedroomType = roomTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room type with id " + id + " not found"));
        
        storedroomType.setName(request.getName());
        storedroomType.setDescription(request.getDescription());
        storedroomType.setCapacity(request.getCapacity());
        storedroomType.setPricePerNight(request.getPricePerNight());

        if (image != null && !image.isEmpty()) {
            // Delete old image if exists
            if (storedroomType.getImageUrl() != null) {
                storageService.delete(storedroomType.getImageUrl());
            }
            String filename = storageService.generateFilename(image.getOriginalFilename());
            String imageUrl = storageService.upload(image, filename);
            storedroomType.setImageUrl(imageUrl);
        }

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

        List<RoomTypeAvailability> roomTypes = roomTypeRepository.findAllRoomTypeAvailabilityByCheckInAndCheckOutOrderByPricePerNightDesc(checkInTime, checkOutTime);
        return roomTypes.stream().map(roomType -> roomTypeMapper.toRoomTypeAvailabilityResponse(roomType)).toList();
    }
}
