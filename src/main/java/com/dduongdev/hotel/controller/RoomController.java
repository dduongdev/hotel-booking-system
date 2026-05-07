package com.dduongdev.hotel.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.CreateRoomRequest;
import com.dduongdev.hotel.payload.request.UpdateRoomRequest;
import com.dduongdev.hotel.payload.response.CreateRoomResponse;
import com.dduongdev.hotel.payload.response.RoomResponse;
import com.dduongdev.hotel.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {
    
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<Page<RoomResponse>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(roomService.getAll(pageable));
    }

    @PostMapping
    public ResponseEntity<CreateRoomResponse> create(@Valid @RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomService.create(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(
        @PathVariable Integer id,
        @Valid @RequestBody UpdateRoomRequest request
    ) {
        RoomResponse response = roomService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<Page<RoomResponse>> getAvailableRoomsByCheckInAndCheckOut(
        @RequestParam String checkIn,
        @RequestParam String checkOut,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(roomService.getAvailableRoomsByCheckInAndCheckOut(LocalDate.parse(checkIn), LocalDate.parse(checkOut), pageable));
    }
}
