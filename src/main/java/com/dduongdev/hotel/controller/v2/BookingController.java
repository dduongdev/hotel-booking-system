package com.dduongdev.hotel.controller.v2;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.v2.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.v1.MakeBookingResponse;
import com.dduongdev.hotel.security.entity.HotelUserDetails;
import com.dduongdev.hotel.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController("BookingControllerV2")
@RequestMapping("/api/v2/bookings")
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
}
