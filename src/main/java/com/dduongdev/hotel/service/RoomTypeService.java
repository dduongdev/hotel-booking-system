package com.dduongdev.hotel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.exception.RoomTypeNotFoundException;
import com.dduongdev.hotel.mapper.RoomTypeMapper;
import com.dduongdev.hotel.payload.request.ChangeRoomTypeHiddenStateRequest;
import com.dduongdev.hotel.payload.request.CreateRoomTypeRequest;
import com.dduongdev.hotel.payload.request.UpdateRoomTypeRequest;
import com.dduongdev.hotel.payload.response.CreateRoomTypeResponse;
import com.dduongdev.hotel.payload.response.RoomTypeResponse;
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
        RoomType storedroomType = roomTypeRepository.findById(id).orElseThrow(() -> new RoomTypeNotFoundException(id));
        
        storedroomType.setName(request.getName());
        storedroomType.setDescription(request.getDescription());
        storedroomType.setCapacity(request.getCapacity());
        storedroomType.setPricePerNight(request.getPricePerNight());

        roomTypeRepository.save(storedroomType);

        return roomTypeMapper.toRoomTypeResponse(storedroomType);
    }

    @Transactional
    public RoomTypeResponse changeHiddenState(Integer id, ChangeRoomTypeHiddenStateRequest request) {
        RoomType storedroomType = roomTypeRepository.findById(id).orElseThrow(() -> new RoomTypeNotFoundException(id));
        
        storedroomType.setHidden(request.isHidden());

        roomTypeRepository.save(storedroomType);

        return roomTypeMapper.toRoomTypeResponse(storedroomType);
    }
}
