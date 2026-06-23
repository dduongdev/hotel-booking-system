package com.dduongdev.hotel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dduongdev.hotel.payload.request.ChangeBranchStatusRequest;
import com.dduongdev.hotel.payload.request.CreateBranchRequest;
import com.dduongdev.hotel.payload.request.UpdateBranchRequest;
import com.dduongdev.hotel.payload.response.ApiResponse;
import com.dduongdev.hotel.payload.response.BranchResponse;
import com.dduongdev.hotel.payload.response.CreateBranchResponse;
import com.dduongdev.hotel.service.BranchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<CreateBranchResponse>> create(@Valid @RequestBody CreateBranchRequest request) {
        CreateBranchResponse response = branchService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Branch created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BranchResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(branchService.getAll(pageable)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<BranchResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBranchRequest request) {
        BranchResponse response = branchService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Branch updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<BranchResponse>> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeBranchStatusRequest request) {
        BranchResponse response = branchService.changeStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Branch status changed successfully", response));
    }
}
