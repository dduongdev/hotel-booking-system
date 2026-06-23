package com.dduongdev.hotel.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dduongdev.hotel.dto.RoomTypeAvailabilityProjection;
import com.dduongdev.hotel.entity.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    Page<RoomType> findAllByBranchIdOrderByPricePerNightDesc(Long branchId, Pageable pageable);

    boolean existsByIdAndBranchId(Long id, Long branchId);

    @Query(value = """
            WITH RoomCounts AS (
                SELECT
                    rt.id AS room_type_id,
                    COUNT(DISTINCT r.id) AS total_rooms,
                    (SELECT COUNT(b.id) FROM booking b
                     WHERE b.room_type_id = rt.id
                       AND b.status IN ('CONFIRMED', 'CHECKED_IN')
                       AND b.check_in < :checkOut
                       AND b.check_out > :checkIn) AS booked_rooms
                FROM room_type rt
                LEFT JOIN room r ON r.room_type_id = rt.id AND r.hidden = false
                WHERE rt.branch_id = :branchId AND rt.hidden = false
                GROUP BY rt.id
            )
            SELECT rt.id AS "id", rt.name AS "name", rt.description AS "description",
                   rt.capacity AS "capacity", rt.price_per_night AS "pricePerNight",
                   (rc.total_rooms - rc.booked_rooms) AS "availableRooms",
                   rt.image_url AS "imageUrl", rt.branch_id AS "branchId"
            FROM room_type rt
            JOIN RoomCounts rc ON rt.id = rc.room_type_id
            WHERE (rc.total_rooms - rc.booked_rooms) > 0
            ORDER BY rt.price_per_night DESC
            """, nativeQuery = true)
    List<RoomTypeAvailabilityProjection> findAvailabilitiesByBranch(Long branchId, LocalDateTime checkIn,
            LocalDateTime checkOut);

    @Query(value = """
            WITH RoomCounts AS (
                SELECT
                    rt.id AS room_type_id,
                    COUNT(DISTINCT r.id) AS total_rooms,
                    (
                        SELECT COUNT(b.id)
                        FROM booking b
                        WHERE b.room_type_id = rt.id
                          AND b.status IN ('CONFIRMED', 'CHECKED_IN')
                          AND b.check_in < :checkOut
                          AND b.check_out > :checkIn
                    ) AS booked_rooms
                FROM room_type rt
                LEFT JOIN room r ON r.room_type_id = rt.id AND r.hidden = false
                WHERE rt.branch_id = :branchId
                  AND rt.id = :id
                  AND rt.hidden = false
                GROUP BY rt.id
            )
            SELECT rt.id AS "id", rt.name AS "name", rt.description AS "description",
                   rt.capacity AS "capacity", rt.price_per_night AS "pricePerNight",
                   (rc.total_rooms - rc.booked_rooms) AS "availableRooms",
                   rt.image_url AS "imageUrl", rt.branch_id AS "branchId"
            FROM room_type rt
            JOIN RoomCounts rc ON rt.id = rc.room_type_id
            """, nativeQuery = true)
    Optional<RoomTypeAvailabilityProjection> findRoomTypeAvailability(
            @Param("id") Long id,
            @Param("branchId") Long branchId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut);
}
