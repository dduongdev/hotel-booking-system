package com.dduongdev.hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.dto.RoomTypeAvailability;
import com.dduongdev.hotel.entity.Booking;
import com.dduongdev.hotel.entity.Branch;
import com.dduongdev.hotel.entity.Room;
import com.dduongdev.hotel.entity.RoomType;
import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.exception.RoomAlreadyBookedException;
import com.dduongdev.hotel.exception.RoomNotBelongToBranchException;
import com.dduongdev.hotel.exception.RoomTypeNotAvailableException;
import com.dduongdev.hotel.exception.RoomTypeNotBelongToBranchException;
import com.dduongdev.hotel.mapper.BookingMapper;
import com.dduongdev.hotel.payload.request.MakeBookingRequest;
import com.dduongdev.hotel.payload.response.BookingResponse;
import com.dduongdev.hotel.payload.response.MakeBookingResponse;
import com.dduongdev.hotel.payload.response.RoomResponse;
import com.dduongdev.hotel.repository.BookingRepository;
import com.dduongdev.hotel.repository.BranchRepository;
import com.dduongdev.hotel.repository.RoomRepository;
import com.dduongdev.hotel.repository.RoomTypeRepository;
import com.dduongdev.hotel.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final com.dduongdev.hotel.mapper.RoomMapper roomMapper;
    private final BranchRepository branchRepository;

    @Transactional
    public MakeBookingResponse make(Long branchId, MakeBookingRequest request, Long userId) {
        if (request.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        if (request.getCheckIn().isAfter(request.getCheckOut())) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date");
        }

        Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new ResourceNotFoundException("Branch not found."));

        if (branch.getStatus() != Branch.Status.ACTIVE) {
            throw new IllegalStateException("Branch is not currently active. Please choose another branch.");
        }

        if (!roomTypeRepository.existsByIdAndBranchId(request.getRoomTypeId(), branchId)) {
            throw new RoomTypeNotBelongToBranchException();
        }

        LocalDateTime checkInTime = LocalDateTime.of(request.getCheckIn(), branch.getCheckInTime());
        LocalDateTime checkOutTime = LocalDateTime.of(request.getCheckOut(), branch.getCheckOutTime());

        RoomTypeAvailability roomTypeAvailability = roomTypeRepository.findRoomTypeAvailability(request.getRoomTypeId(), branchId, checkInTime, checkOutTime);

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
        booking.setBranch(branch);

        bookingRepository.save(booking);

        return bookingMapper.toMakeBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getByUserId(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable).map(bookingMapper::toBookingResponse);
    }

    @Transactional
    public void cancel(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking is not found or you don't have permission."));

        validateCancellable(booking);

        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);
    }

    public Page<BookingResponse> getAll(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toBookingResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllByBranch(Long branchId, Pageable pageable) {
        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch with id " + branchId + " not found");
        }
        return bookingRepository.findByBranchId(branchId, pageable).map(bookingMapper::toBookingResponse);
    }

    @Transactional
    public BookingResponse cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking is not found"));

        validateCancellable(booking);

        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getBestFitRooms(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + bookingId + " not found"));

        Long branchId = booking.getBranch() != null ? booking.getBranch().getId() : null;
        List<Room> rooms = roomRepository.findBestFitForBooking(
                booking.getRoomType().getId(),
                branchId,
                booking.getCheckIn(),
                booking.getCheckOut());

        return rooms.stream()
                .map(roomMapper::toRoomResponse)
                .toList();
    }

    @Transactional
    public BookingResponse checkIn(Long bookingId, Long roomId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + bookingId + " not found"));

        if (booking.getStatus() != Booking.Status.CONFIRMED) {
            throw new IllegalStateException(
                    "Cannot check in. Booking status must be CONFIRMED, but current status is " + booking.getStatus());
        }

        if (booking.getCheckIn().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Cannot check in. Check-in date (" + booking.getCheckIn() + ") has not arrived yet");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + roomId + " not found"));

        if (room.isHidden()) {
            throw new IllegalStateException("Room '" + room.getName() + "' is hidden and cannot be assigned");
        }

        if (!room.getRoomType().getId().equals(booking.getRoomType().getId())) {
            throw new IllegalArgumentException(
                    "Room '" + room.getName() + "' does not belong to the booking's room type");
        }

        if (room.getBranch() == null || !room.getBranch().getId().equals(booking.getBranch().getId())) {
            throw new RoomNotBelongToBranchException();
        }

        boolean hasOverlap = bookingRepository.existsByRoomIdAndOverlappingDates(
                roomId, booking.getCheckIn(), booking.getCheckOut(), bookingId);
        if (hasOverlap) {
            throw new RoomAlreadyBookedException(
                    roomId, booking.getCheckIn().toString(), booking.getCheckOut().toString());
        }

        booking.setRoom(room);
        booking.setStatus(Booking.Status.CHECKED_IN);

        room.setBookingCount(room.getBookingCount() + 1);

        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    @Transactional
    public BookingResponse checkOut(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + bookingId + " not found"));

        if (booking.getStatus() != Booking.Status.CHECKED_IN) {
            throw new IllegalStateException(
                    "Cannot check out. Booking status must be CHECKED_IN, but current status is " + booking.getStatus());
        }

        booking.setStatus(Booking.Status.CHECKED_OUT);
        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    private void validateCancellable(Booking booking) {
        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        if (booking.getStatus() == Booking.Status.CHECKED_IN) {
            throw new IllegalStateException("Cannot cancel a booking that has already been checked in");
        }

        if (booking.getStatus() == Booking.Status.CHECKED_OUT) {
            throw new IllegalStateException("Cannot cancel a booking that has already been checked out");
        }

        if (booking.getStatus() == Booking.Status.CONFIRMED && booking.getCheckIn().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot cancel a confirmed booking that has already started");
        }
    }
}
