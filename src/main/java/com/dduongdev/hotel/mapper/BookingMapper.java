package com.dduongdev.hotel.mapper;

import org.springframework.stereotype.Component;

import com.dduongdev.hotel.entity.Booking;
import com.dduongdev.hotel.payload.response.BookingResponse;
import com.dduongdev.hotel.payload.response.MakeBookingResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final RoomMapper roomMapper;

    public MakeBookingResponse toMakeBookingResponse(Booking booking) {
        return new MakeBookingResponse(
                booking.getId(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                roomMapper.toRoomResponse(booking.getRoom()),
                booking.getCreatedAt());
    }

    public BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                roomMapper.toRoomResponse(booking.getRoom()),
                booking.getUser() != null ? booking.getUser().getId() : null,
                booking.getUser() != null ? booking.getUser().getUsername() : null,
                booking.getCreatedAt(),
                booking.getUpdatedAt());
    }
}
