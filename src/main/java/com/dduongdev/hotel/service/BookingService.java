package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.dto.RoomTypeAvailability;
import com.dduongdev.hotel.entity.Booking;
import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.exception.RoomTypeNotAvailableException;
import com.dduongdev.hotel.mapper.BookingMapper;
import com.dduongdev.hotel.payload.request.CancelOwnBookingRequest;
import com.dduongdev.hotel.payload.request.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.BookingResponse;
import com.dduongdev.hotel.payload.response.MakeBookingResponse;
import com.dduongdev.hotel.repository.BookingRepository;
import com.dduongdev.hotel.repository.RoomTypeRepository;
import com.dduongdev.hotel.repository.UserRepository;
import com.dduongdev.hotel.util.Constants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Transactional
    public MakeBookingResponse make(Integer userId, MakeBookingRequest request) {
        if (request.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        if (request.getCheckIn().isAfter(request.getCheckOut())) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date");
        }

        LocalDateTime checkInTime = LocalDateTime.of(request.getCheckIn(), Constants.CHECK_IN_TIME);
        LocalDateTime checkOutTime = LocalDateTime.of(request.getCheckOut(), Constants.CHECK_OUT_TIME);

        RoomTypeAvailability roomTypeAvailability = roomTypeRepository.findRoomTypeAvailabilityByIdAndCheckInAndCheckOut(request.getRoomTypeId(), checkInTime, checkOutTime);

        if (roomTypeAvailability == null || roomTypeAvailability.getAvailableRoomCount() <= 0) {
            throw new RoomTypeNotAvailableException(request.getRoomTypeId(), request.getCheckIn().toString(), request.getCheckOut().toString());
        }

        RoomType roomTypeProxy = roomTypeRepository.getReferenceById(roomTypeAvailability.getId());

        User userProxy = userRepository.getReferenceById(userId);

        Booking booking = new Booking();
        booking.setCheckIn(checkInTime);
        booking.setCheckOut(checkOutTime);
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setRoomType(roomTypeProxy);
        booking.setUser(userProxy);

        bookingRepository.save(booking);

        return bookingMapper.toMakeBookingResponse(booking);
    }

    public Page<BookingResponse> getByUserId(Integer userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable).map(bookingMapper::toBookingResponse);
    }

    @Transactional
    public void cancel(Integer userId, CancelOwnBookingRequest request) {
        Booking booking = bookingRepository.findByIdAndUserId(request.getBookingId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking is not found or you don't have permission"));

        if (booking.getStatus().equals(Booking.Status.CANCELLED)) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        if (booking.getStatus().equals(Booking.Status.CHECKED_IN)) {
            throw new IllegalStateException("Cannot cancel a booking that has already been checked in");
        }

        if (booking.getStatus().equals(Booking.Status.CHECKED_OUT)) {
            throw new IllegalStateException("Cannot cancel a booking that has already been checked out");
        }

        if (booking.getStatus().equals(Booking.Status.CONFIRMED) && booking.getCheckIn().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot cancel a confirmed booking that has already started");
        }

        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);
    }

    public Page<BookingResponse> getAll(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toBookingResponse);
    }

    @Transactional
    public BookingResponse cancel(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking is not found"));

        if (booking.getStatus().equals(Booking.Status.CANCELLED)) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        if (booking.getStatus().equals(Booking.Status.CHECKED_IN)) {
            throw new IllegalStateException("Cannot cancel a booking that has already been checked in");
        }

        if (booking.getStatus().equals(Booking.Status.CHECKED_OUT)) {
            throw new IllegalStateException("Cannot cancel a booking that has already been checked out");
        }

        if (booking.getStatus().equals(Booking.Status.CONFIRMED) && booking.getCheckIn().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot cancel a confirmed booking that has already started");
        }

        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }
}
