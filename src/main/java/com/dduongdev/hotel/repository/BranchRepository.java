package com.dduongdev.hotel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dduongdev.hotel.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Page<Branch> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
