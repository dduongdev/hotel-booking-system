package com.dduongdev.hotel.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.BookingResponse;
import com.dduongdev.hotel.payload.response.MakeBookingResponse;
import com.dduongdev.hotel.security.entity.HotelUserDetails;
import com.dduongdev.hotel.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<MakeBookingResponse> make(@Valid @RequestBody MakeBookingRequest request, @AuthenticationPrincipal HotelUserDetails userDetails) {
        Integer userId = userDetails.getId();
        MakeBookingResponse response = bookingService.make(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> getBookingsOfCurrentUser(@AuthenticationPrincipal HotelUserDetails userDetails) {
        Integer userId = userDetails.getId();
        List<BookingResponse> responses = bookingService.getByUserId(userId);
        return ResponseEntity.ok(responses);
    }
}
