package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.Booking;
import com.dduongdev.hotel.entity.Room;
import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.mapper.BookingMapper;
import com.dduongdev.hotel.payload.request.CancelOwnBookingRequest;
import com.dduongdev.hotel.payload.request.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.BookingResponse;
import com.dduongdev.hotel.payload.response.MakeBookingResponse;
import com.dduongdev.hotel.repository.BookingRepository;
import com.dduongdev.hotel.repository.RoomRepository;
import com.dduongdev.hotel.repository.UserRepository;
import com.dduongdev.hotel.util.Constants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
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

        LocalDateTime requestCheckInTime = LocalDateTime.of(request.getCheckIn(), Constants.CHECK_IN_TIME);
        LocalDateTime requestCheckOutTime = LocalDateTime.of(request.getCheckOut(), Constants.CHECK_OUT_TIME);

        Room availableRoom = roomRepository
                .findTopAvailableByRoomTypeIdAndDateRange(
                        request.getRoomTypeId(),
                        requestCheckInTime,
                        requestCheckOutTime
                )
                .orElseThrow(() -> new IllegalArgumentException("Room type " + request.getRoomTypeId()
                        + " not found or has no available rooms for the selected dates"));

        
        availableRoom.setBookingCount(availableRoom.getBookingCount() + 1);
        roomRepository.save(availableRoom);

        User userProxy = userRepository.getReferenceById(userId);

        Booking booking = new Booking();
        booking.setCheckIn(LocalDateTime.of(request.getCheckIn(), Constants.CHECK_IN_TIME));
        booking.setCheckOut(LocalDateTime.of(request.getCheckOut(), Constants.CHECK_OUT_TIME));
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setRoom(availableRoom);
        booking.setUser(userProxy);

        bookingRepository.save(booking);

        return bookingMapper.toMakeBookingResponse(booking);
    }

    public Page<BookingResponse> getByUserId(Integer userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable).map(bookingMapper::toBookingResponse);
    }

    public void cancel(Integer userId, CancelOwnBookingRequest request) {
        Booking booking = bookingRepository.findByIdAndUserId(request.getBookingId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking with id " + request.getBookingId() + " not found for user with id " + userId));

        if (booking.getStatus().equals(Booking.Status.CONFIRMED) && booking.getCheckIn().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot cancel a confirmed booking that has already started");
        }

        if (booking.getStatus().equals(Booking.Status.CANCELLED)) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        Room bookedRoom = booking.getRoom();
        bookedRoom.setBookingCount(bookedRoom.getBookingCount() - 1);
        roomRepository.save(bookedRoom);

        bookingRepository.delete(booking);
    }

    public Page<BookingResponse> getAll(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toBookingResponse);
    }

    public BookingResponse cancel(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + id + " not found"));

        if (booking.getStatus().equals(Booking.Status.CONFIRMED) && booking.getCheckIn().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot cancel a confirmed booking that has already started");
        }

        if (booking.getStatus().equals(Booking.Status.CANCELLED)) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        Room bookedRoom = booking.getRoom();
        bookedRoom.setBookingCount(bookedRoom.getBookingCount() - 1);
        roomRepository.save(bookedRoom);

        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }
}
