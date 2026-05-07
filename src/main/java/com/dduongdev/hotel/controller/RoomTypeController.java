package com.dduongdev.hotel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.ChangeRoomTypeHiddenStateRequest;
import com.dduongdev.hotel.payload.request.CreateRoomTypeRequest;
import com.dduongdev.hotel.payload.request.UpdateRoomTypeRequest;
import com.dduongdev.hotel.payload.response.CreateRoomTypeResponse;
import com.dduongdev.hotel.payload.response.RoomTypeResponse;
import com.dduongdev.hotel.service.RoomTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/room-types")
@RequiredArgsConstructor
public class RoomTypeController {
    
    private final RoomTypeService roomTypeService;

    @PostMapping
    public ResponseEntity<CreateRoomTypeResponse> createRoomType(@Valid @RequestBody CreateRoomTypeRequest request) {
        CreateRoomTypeResponse response = roomTypeService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<RoomTypeResponse>> getAllRoomTypes(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(roomTypeService.getAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeResponse> updateRoomType(@PathVariable Integer id, @Valid @RequestBody UpdateRoomTypeRequest request) {
        RoomTypeResponse response = roomTypeService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/hidden")
    public ResponseEntity<RoomTypeResponse> changeRoomTypeHiddenState(@PathVariable Integer id, @Valid @RequestBody ChangeRoomTypeHiddenStateRequest request) {
        RoomTypeResponse response = roomTypeService.changeHiddenState(id, request);
        return ResponseEntity.ok(response);
    }
}
