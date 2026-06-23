package com.dduongdev.hotel.service;

import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

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
    private final StorageService storageService;

    @Transactional
    public CreateBranchResponse create(CreateBranchRequest request, MultipartFile image) {
        Branch branch = new Branch();

        branch.setName(request.getName());
        branch.setDescription(request.getDescription());
        branch.setPhoneNumber(request.getPhoneNumber());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setLatitude(request.getLatitude());
        branch.setLongitude(request.getLongitude());
        branch.setCheckInTime(LocalTime.parse(request.getCheckInTime()));
        branch.setCheckOutTime(LocalTime.parse(request.getCheckOutTime()));

        if (image != null && !image.isEmpty()) {
            String filename = storageService.generateFilename(image.getOriginalFilename());
            String imageUrl = storageService.upload(image, filename);
            branch.setImageUrl(imageUrl);
        }

        branchRepository.save(branch);

        return branchMapper.toCreateBranchResponse(branch);
    }

    @Transactional(readOnly = true)
    public Page<BranchResponse> getAll(Pageable pageable) {
        Page<Branch> branches = branchRepository.findAllByOrderByCreatedAtDesc(pageable);
        return branches.map(branchMapper::toBranchResponse);
    }

    @Transactional(readOnly = true)
    public BranchResponse getById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id " + id + " not found"));
        return branchMapper.toBranchResponse(branch);
    }

    @Transactional
    public BranchResponse update(Long id, UpdateBranchRequest request, MultipartFile image) {
        Branch storedBranch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id " + id + " not found"));

        storedBranch.setName(request.getName());
        storedBranch.setDescription(request.getDescription());
        storedBranch.setPhoneNumber(request.getPhoneNumber());
        storedBranch.setEmail(request.getEmail());
        storedBranch.setAddress(request.getAddress());
        storedBranch.setCity(request.getCity());
        storedBranch.setLatitude(request.getLatitude());
        storedBranch.setLongitude(request.getLongitude());
        storedBranch.setCheckInTime(LocalTime.parse(request.getCheckInTime()));
        storedBranch.setCheckOutTime(LocalTime.parse(request.getCheckOutTime()));

        if (image != null && !image.isEmpty()) {
            if (storedBranch.getImageUrl() != null) {
                storageService.delete(storedBranch.getImageUrl());
            }
            String filename = storageService.generateFilename(image.getOriginalFilename());
            String imageUrl = storageService.upload(image, filename);
            storedBranch.setImageUrl(imageUrl);
        }

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
