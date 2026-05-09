package com.dduongdev.hotel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.CancelOwnBookingRequest;
import com.dduongdev.hotel.payload.request.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.BookingResponse;
import com.dduongdev.hotel.payload.response.MakeBookingResponse;
import com.dduongdev.hotel.security.entity.HotelUserDetails;
import com.dduongdev.hotel.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController("BookingControllerV1")
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<MakeBookingResponse> make(@Valid @RequestBody MakeBookingRequest request, @AuthenticationPrincipal HotelUserDetails userDetails) {
        Integer userId = userDetails.getId();
        MakeBookingResponse response = bookingService.make(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<BookingResponse>> getBookingsOfCurrentUser(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @AuthenticationPrincipal HotelUserDetails userDetails
    ) {
        Integer userId = userDetails.getId();

        Pageable pageable = PageRequest.of(page, size);

        Page<BookingResponse> responses = bookingService.getByUserId(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/me/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> cancelBookingOfCurrentUser(@Valid @RequestBody CancelOwnBookingRequest request, @AuthenticationPrincipal HotelUserDetails userDetails) {
        Integer userId = userDetails.getId();
        bookingService.cancel(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Page<BookingResponse>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> responses = bookingService.getAll(pageable);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Integer id) {
        BookingResponse response = bookingService.cancel(id);
        return ResponseEntity.ok(response);
    }
}
