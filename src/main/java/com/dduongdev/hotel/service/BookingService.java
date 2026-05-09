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
import com.dduongdev.hotel.mapper.BookingMapper;
import com.dduongdev.hotel.payload.request.CancelOwnBookingRequest;
import com.dduongdev.hotel.payload.request.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.BookingResponse;
import com.dduongdev.hotel.payload.response.MakeBookingResponse;
import com.dduongdev.hotel.repository.BookingRepository;
import com.dduongdev.hotel.repository.RoomRepository;
import com.dduongdev.hotel.repository.UserRepository;

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

        List<RoomBookingCount> availableRoomBookingCounts = roomRepository
                .countBookingsForAvailableRoomsByRoomType(
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
        booking.setStatus(Booking.Status.CONFIRMED);
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

        if (booking.getStatus().equals(Booking.Status.CONFIRMED) && booking.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot cancel a confirmed booking that has already started");
        }

        bookingRepository.delete(booking);
    }

    public Page<BookingResponse> getAll(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toBookingResponse);
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
