package com.dduongdev.hotel.service;

import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.Branch;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.mapper.BranchMapper;
import com.dduongdev.hotel.payload.request.ChangeBranchStatusRequest;
import com.dduongdev.hotel.payload.request.CreateBranchRequest;
import com.dduongdev.hotel.payload.request.UpdateBranchRequest;
import com.dduongdev.hotel.payload.response.BranchResponse;
import com.dduongdev.hotel.payload.response.CreateBranchResponse;
import com.dduongdev.hotel.repository.BranchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    public CreateBranchResponse create(CreateBranchRequest request) {
        Branch branch = new Branch();

        branch.setDescription(request.getDescription());
        branch.setPhoneNumber(request.getPhoneNumber());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setLatitude(request.getLatitude());
        branch.setLongitude(request.getLongitude());
        branch.setCheckInTime(LocalTime.parse(request.getCheckInTime()));
        branch.setCheckOutTime(LocalTime.parse(request.getCheckOutTime()));

        branchRepository.save(branch);

        return branchMapper.toCreateBranchResponse(branch);
    }

    public Page<BranchResponse> getAll(Pageable pageable) {
        Page<Branch> branches = branchRepository.findAllByOrderByCreatedAtDesc(pageable);
        return branches.map(branchMapper::toBranchResponse);
    }

    @Transactional
    public BranchResponse update(Long id, UpdateBranchRequest request) {
        Branch storedBranch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id " + id + " not found"));

        storedBranch.setDescription(request.getDescription());
        storedBranch.setPhoneNumber(request.getPhoneNumber());
        storedBranch.setEmail(request.getEmail());
        storedBranch.setAddress(request.getAddress());
        storedBranch.setCity(request.getCity());
        storedBranch.setLatitude(request.getLatitude());
        storedBranch.setLongitude(request.getLongitude());
        storedBranch.setCheckInTime(LocalTime.parse(request.getCheckInTime()));
        storedBranch.setCheckOutTime(LocalTime.parse(request.getCheckOutTime()));

        branchRepository.save(storedBranch);

        return branchMapper.toBranchResponse(storedBranch);
    }

    @Transactional
    public BranchResponse changeStatus(Long id, ChangeBranchStatusRequest request) {
        Branch storedBranch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id " + id + " not found"));

        storedBranch.setStatus(request.getStatus());

        branchRepository.save(storedBranch);

        return branchMapper.toBranchResponse(storedBranch);
    }
}
