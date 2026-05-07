package com.dduongdev.hotel.mapper;

import org.springframework.stereotype.Component;

import com.dduongdev.hotel.entity.Booking;
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
}
