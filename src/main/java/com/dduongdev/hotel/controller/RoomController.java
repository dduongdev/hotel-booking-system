package com.dduongdev.hotel.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.dduongdev.hotel.payload.response.ApiResponse;
import com.dduongdev.hotel.payload.response.CreateRoomResponse;
import com.dduongdev.hotel.payload.response.RoomResponse;
import com.dduongdev.hotel.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/branches/{branchId}/rooms")
@RequiredArgsConstructor
public class RoomController {
    
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> getAllByBranch(
        @PathVariable Long branchId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllByBranch(branchId, pageable)));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<CreateRoomResponse>> create(
        @PathVariable Long branchId,
        @Valid @RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomService.create(branchId, request);
        return ResponseEntity.ok(ApiResponse.success("Room created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<RoomResponse>> update(
        @PathVariable Long branchId,
        @PathVariable Long id,
        @Valid @RequestBody UpdateRoomRequest request
    ) {
        RoomResponse response = roomService.update(id, branchId, request);
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", response));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> getAvailableRoomsByBranch(
        @PathVariable Long branchId,
        @RequestParam String checkIn,
        @RequestParam String checkOut,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
            roomService.getAvailableRoomsByCheckInAndCheckOut(branchId, LocalDate.parse(checkIn), LocalDate.parse(checkOut), pageable)));
    }
}
