package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.Booking;
import com.dduongdev.hotel.entity.Room;
import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.exception.RoomAlreadyBookedException;
import com.dduongdev.hotel.mapper.BookingMapper;
import com.dduongdev.hotel.payload.request.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.BookingResponse;
import com.dduongdev.hotel.payload.response.MakeBookingResponse;
import com.dduongdev.hotel.repository.BookingRepository;
import com.dduongdev.hotel.repository.RoomRepository;
import com.dduongdev.hotel.repository.UserRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;
    private final BookingMapper bookingMapper;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public MakeBookingResponse make(Integer userId, MakeBookingRequest request) {

        if (request.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        if (request.getCheckIn().isAfter(request.getCheckOut())) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date");
        }

        List<Booking> overlappingBookings = bookingRepository.findByRoomIdAndCheckInAndCheckOutOverlap(
            request.getRoomId(),
            request.getCheckIn(),
            request.getCheckOut()
        );

        if (!overlappingBookings.isEmpty()) {
            throw new RoomAlreadyBookedException(
                request.getRoomId(),
                request.getCheckIn().toString(),
                request.getCheckOut().toString()
            );
        }

        if (!roomRepository.existsById(request.getRoomId())) {
            throw new ResourceNotFoundException("Room with id " + request.getRoomId() + " not found");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }

        Room roomProxy = entityManager.getReference(Room.class, request.getRoomId());
        User userProxy = entityManager.getReference(User.class, userId);

        Booking booking = new Booking();
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setRoom(roomProxy);
        booking.setUser(userProxy);
        
        bookingRepository.save(booking);

        return bookingMapper.toMakeBookingResponse(booking);
    }

    public List<BookingResponse> getByUserId(Integer userId) {
        return bookingRepository.findByUserId(userId).stream().map(bookingMapper::toBookingResponse).toList();
    }
}
