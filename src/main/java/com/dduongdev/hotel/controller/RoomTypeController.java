package com.dduongdev.hotel.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.ChangeRoomTypeHiddenStateRequest;
import com.dduongdev.hotel.payload.request.CreateRoomTypeRequest;
import com.dduongdev.hotel.payload.request.UpdateRoomTypeRequest;
import com.dduongdev.hotel.payload.response.ApiResponse;
import com.dduongdev.hotel.payload.response.CreateRoomTypeResponse;
import com.dduongdev.hotel.payload.response.RoomTypeAvailabilityResponse;
import com.dduongdev.hotel.payload.response.RoomTypeResponse;
import com.dduongdev.hotel.service.RoomTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/branches/{branchId}/room-types")
@RequiredArgsConstructor
public class RoomTypeController {
    
    private final RoomTypeService roomTypeService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<CreateRoomTypeResponse>> create(
            @PathVariable Long branchId,
            @Valid @ModelAttribute CreateRoomTypeRequest request) {
        CreateRoomTypeResponse response = roomTypeService.create(branchId, request);
        return ResponseEntity.ok(ApiResponse.success("Room type created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoomTypeResponse>>> getAllByBranch(
        @PathVariable Long branchId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getAllByBranch(branchId, pageable)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> update(
            @PathVariable Long branchId,
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateRoomTypeRequest request) {
        RoomTypeResponse response = roomTypeService.update(id, branchId, request);
        return ResponseEntity.ok(ApiResponse.success("Room type updated successfully", response));
    }

    @PatchMapping("/{id}/hidden")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> changeHiddenState(
        @PathVariable Long branchId,
        @PathVariable Long id, 
        @Valid @RequestBody ChangeRoomTypeHiddenStateRequest request) {
        RoomTypeResponse response = roomTypeService.changeHiddenState(id, branchId, request);
        return ResponseEntity.ok(ApiResponse.success("Room type hidden state changed successfully", response));
    }

    @GetMapping("/availabilities")
    public ResponseEntity<ApiResponse<List<RoomTypeAvailabilityResponse>>> getAvailabilitiesByBranch(
        @PathVariable Long branchId,
        @RequestParam String checkIn, 
        @RequestParam String checkOut) {
        List<RoomTypeAvailabilityResponse> response = roomTypeService.getAvailabilitiesByBranch(branchId, LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
