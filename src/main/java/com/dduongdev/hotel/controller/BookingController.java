package com.dduongdev.hotel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.CheckInRequest;
import com.dduongdev.hotel.payload.request.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.ApiResponse;
import com.dduongdev.hotel.payload.response.BookingResponse;
import com.dduongdev.hotel.payload.response.MakeBookingResponse;
import com.dduongdev.hotel.payload.response.RoomResponse;
import com.dduongdev.hotel.security.entity.HotelUserDetails;
import com.dduongdev.hotel.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;

    @PostMapping("/api/v1/branches/{branchId}/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<MakeBookingResponse>> make(
        @PathVariable Long branchId,
        @Valid @RequestBody MakeBookingRequest request, 
        @AuthenticationPrincipal HotelUserDetails userDetails) {
        Long userId = userDetails.getId();
        MakeBookingResponse response = bookingService.make(branchId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully", response));
    }

    @GetMapping("/api/v1/bookings/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getBookingsOfCurrentUser(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @AuthenticationPrincipal HotelUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> responses = bookingService.getByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PatchMapping("/api/v1/bookings/me/{bookingId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> cancelBookingOfCurrentUser(
        @PathVariable Long bookingId,
        @AuthenticationPrincipal HotelUserDetails userDetails) {
        Long userId = userDetails.getId();
        bookingService.cancel(userId, bookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", null));
    }

    @GetMapping("/api/v1/branches/{branchId}/bookings")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getAllByBranch(
        @PathVariable Long branchId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> responses = bookingService.getAllByBranch(branchId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/api/v1/bookings")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> responses = bookingService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PatchMapping("/api/v1/bookings/{id}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", response));
    }

    @GetMapping("/api/v1/bookings/{id}/best-fit-rooms")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getBestFitRooms(@PathVariable Long id) {
        List<RoomResponse> rooms = bookingService.getBestFitRooms(id);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @PatchMapping("/api/v1/bookings/{id}/check-in")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<BookingResponse>> checkIn(
            @PathVariable Long id,
            @Valid @RequestBody CheckInRequest request) {
        BookingResponse response = bookingService.checkIn(id, request.getRoomId());
        return ResponseEntity.ok(ApiResponse.success("Check-in completed successfully", response));
    }

    @PatchMapping("/api/v1/bookings/{id}/check-out")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<BookingResponse>> checkOut(@PathVariable Long id) {
        BookingResponse response = bookingService.checkOut(id);
        return ResponseEntity.ok(ApiResponse.success("Check-out completed successfully", response));
    }
}
