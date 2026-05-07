package com.dduongdev.hotel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dduongdev.hotel.entity.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    Page<RoomType> findAll(Pageable pageable);
}
