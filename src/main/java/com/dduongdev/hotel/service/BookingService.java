package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.dto.RoomBookingCount;
import com.dduongdev.hotel.entity.Booking;
import com.dduongdev.hotel.entity.Room;
import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.exception.RoomAlreadyBookedException;
import com.dduongdev.hotel.mapper.BookingMapper;
import com.dduongdev.hotel.payload.request.v1.CancelOwnBookingRequest;
import com.dduongdev.hotel.payload.request.v1.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.v1.BookingResponse;
import com.dduongdev.hotel.payload.response.v1.MakeBookingResponse;
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

        boolean isRoomBooked = bookingRepository.existsByRoomIdAndCheckInAndCheckOutOverlap(
                request.getRoomId(),
                request.getCheckIn(),
                request.getCheckOut());

        if (isRoomBooked) {
            throw new RoomAlreadyBookedException(
                    request.getRoomId(),
                    request.getCheckIn().toString(),
                    request.getCheckOut().toString());
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

    @Transactional
    public MakeBookingResponse make(Integer userId, com.dduongdev.hotel.payload.request.v2.MakeBookingRequest request) {
        if (request.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        if (request.getCheckIn().isAfter(request.getCheckOut())) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date");
        }

        List<RoomBookingCount> availableRoomBookingCounts = roomRepository
                .countBookingsOfAvailableRoomsByRoomTypeIdAndCheckInAndCheckOut(
                        request.getRoomTypeId(),
                        request.getCheckIn(),
                        request.getCheckOut()
                );

        if (availableRoomBookingCounts.isEmpty()) {
            throw new IllegalArgumentException("Room type " + request.getRoomTypeId()
                    + " not found or has no available rooms for the selected dates");
        }

        RoomBookingCount leastBookedRoom = availableRoomBookingCounts.stream()
                .min((a, b) -> Long.compare(a.getBookingCount(), b.getBookingCount()))
                .orElseThrow(() -> new IllegalStateException("No available rooms found for the selected dates"));

        Room roomProxy = roomRepository.getReferenceById(leastBookedRoom.getId());
        User userProxy = userRepository.getReferenceById(userId);

        Booking booking = new Booking();
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setRoom(roomProxy);
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

        if (!(booking.getStatus().equals(Booking.Status.PENDING))) {
            throw new IllegalStateException("Only pending bookings can be canceled");
        }

        bookingRepository.delete(booking);
    }

    public Page<BookingResponse> getAll(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toBookingResponse);
    }

    public BookingResponse confirm(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + id + " not found"));

        if (!(booking.getStatus().equals(Booking.Status.PENDING))) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }

        booking.setStatus(Booking.Status.CONFIRMED);
        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    public BookingResponse cancel(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + id + " not found"));

        if (booking.getStatus().equals(Booking.Status.CONFIRMED) && booking.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot cancel a confirmed booking that has already started");
        }

        if (booking.getStatus().equals(Booking.Status.CANCELLED)) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }
}
