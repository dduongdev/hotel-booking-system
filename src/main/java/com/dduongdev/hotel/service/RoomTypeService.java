package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.dto.RoomTypeAvailability;
import com.dduongdev.hotel.entity.Branch;
import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.exception.RoomTypeNotBelongToBranchException;
import com.dduongdev.hotel.mapper.RoomTypeMapper;
import com.dduongdev.hotel.payload.request.ChangeRoomTypeHiddenStateRequest;
import com.dduongdev.hotel.payload.request.CreateRoomTypeRequest;
import com.dduongdev.hotel.payload.request.UpdateRoomTypeRequest;
import com.dduongdev.hotel.payload.response.CreateRoomTypeResponse;
import com.dduongdev.hotel.payload.response.RoomTypeAvailabilityResponse;
import com.dduongdev.hotel.payload.response.RoomTypeResponse;
import com.dduongdev.hotel.repository.BranchRepository;
import com.dduongdev.hotel.repository.RoomTypeRepository;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;
    private final StorageService storageService;
    private final BranchRepository branchRepository;

    @Transactional
    public CreateRoomTypeResponse create(Long branchId, CreateRoomTypeRequest request) {
        Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        RoomType roomType = new RoomType();

        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setCapacity(request.getCapacity());
        roomType.setPricePerNight(request.getPricePerNight());
        roomType.setBranch(branch);

        MultipartFile image = request.getImage();

        if (image != null && !image.isEmpty()) {
            String filename = storageService.generateFilename(image.getOriginalFilename());
            String imageUrl = storageService.upload(image, filename);
            roomType.setImageUrl(imageUrl);
        }

        roomTypeRepository.save(roomType);

        return roomTypeMapper.toCreateRoomTypeResponse(roomType);
    }

    @Transactional(readOnly = true)
    public Page<RoomTypeResponse> getAllByBranch(Long branchId, Pageable pageable) {
        Page<RoomType> roomTypes = roomTypeRepository.findAllByBranchIdOrderByPricePerNightDesc(branchId, pageable);
        return roomTypes.map(roomTypeMapper::toRoomTypeResponse);
    }

    @Transactional
    public RoomTypeResponse update(Long id, Long branchId, UpdateRoomTypeRequest request) {
        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch with id " + branchId + " not found");
        }

        RoomType storedroomType = roomTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room type with id " + id + " not found"));
        
        if (storedroomType.getBranch() == null || !storedroomType.getBranch().getId().equals(branchId)) {
            throw new RoomTypeNotBelongToBranchException();
        }

        storedroomType.setName(request.getName());
        storedroomType.setDescription(request.getDescription());
        storedroomType.setCapacity(request.getCapacity());
        storedroomType.setPricePerNight(request.getPricePerNight());

        MultipartFile image = request.getImage();

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
    public RoomTypeResponse changeHiddenState(Long id, Long branchId, ChangeRoomTypeHiddenStateRequest request) {
        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch with id " + branchId + " not found");
        }

        RoomType storedroomType = roomTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room type with id " + id + " not found"));
        
        if (storedroomType.getBranch() == null || !storedroomType.getBranch().getId().equals(branchId)) {
            throw new RoomTypeNotBelongToBranchException();
        }

        storedroomType.setHidden(request.isHidden());

        roomTypeRepository.save(storedroomType);

        return roomTypeMapper.toRoomTypeResponse(storedroomType);
    }

    @Transactional(readOnly = true)
    public List<RoomTypeAvailabilityResponse> getAvailabilitiesByBranch(Long branchId, LocalDate checkIn, LocalDate checkOut) {
        Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

         if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        LocalDateTime checkInTime = LocalDateTime.of(checkIn, branch.getCheckInTime());
        LocalDateTime checkOutTime = LocalDateTime.of(checkOut, branch.getCheckOutTime());

        List<RoomTypeAvailability> roomTypes = roomTypeRepository.findAvailabilitiesByBranch(branch.getId(), checkInTime, checkOutTime);
        return roomTypes.stream().map(roomType -> roomTypeMapper.toRoomTypeAvailabilityResponse(roomType)).toList();
    }
}
