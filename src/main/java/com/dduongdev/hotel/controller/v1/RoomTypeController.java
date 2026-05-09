package com.dduongdev.hotel.controller.v1;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.v1.ChangeRoomTypeHiddenStateRequest;
import com.dduongdev.hotel.payload.request.v1.CreateRoomTypeRequest;
import com.dduongdev.hotel.payload.request.v1.UpdateRoomTypeRequest;
import com.dduongdev.hotel.payload.response.v1.CreateRoomTypeResponse;
import com.dduongdev.hotel.payload.response.v1.RoomTypeAvailabilityResponse;
import com.dduongdev.hotel.payload.response.v1.RoomTypeResponse;
import com.dduongdev.hotel.service.RoomTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/room-types")
@RequiredArgsConstructor
public class RoomTypeController {
    
    private final RoomTypeService roomTypeService;

    @PostMapping
    // @PreAuthorize("hasRole('MANAGER')")
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
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RoomTypeResponse> updateRoomType(@PathVariable Integer id, @Valid @RequestBody UpdateRoomTypeRequest request) {
        RoomTypeResponse response = roomTypeService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/hidden")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RoomTypeResponse> changeRoomTypeHiddenState(@PathVariable Integer id, @Valid @RequestBody ChangeRoomTypeHiddenStateRequest request) {
        RoomTypeResponse response = roomTypeService.changeHiddenState(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/availability")
    public ResponseEntity<List<RoomTypeAvailabilityResponse>> getRoomTypeAvailability(
        @RequestParam String checkIn, 
        @RequestParam String checkOut) {
        List<RoomTypeAvailabilityResponse> response = roomTypeService.getRoomTypeAvailability(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        return ResponseEntity.ok(response);
    }
}
