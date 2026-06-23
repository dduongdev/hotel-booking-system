package com.dduongdev.hotel.mapper;

import org.springframework.stereotype.Component;

import com.dduongdev.hotel.entity.Branch;
import com.dduongdev.hotel.payload.response.BranchResponse;
import com.dduongdev.hotel.payload.response.CreateBranchResponse;

@Component
public class BranchMapper {

    public BranchResponse toBranchResponse(Branch branch) {
        return new BranchResponse(
                branch.getId(),
                branch.getDescription(),
                branch.getPhoneNumber(),
                branch.getEmail(),
                branch.getAddress(),
                branch.getCity(),
                branch.getLatitude(),
                branch.getLongitude(),
                branch.getCheckInTime().toString(),
                branch.getCheckOutTime().toString(),
                branch.getStatus(),
                branch.getCreatedAt(),
                branch.getUpdatedAt()
        );
    }

    public CreateBranchResponse toCreateBranchResponse(Branch branch) {
        return new CreateBranchResponse(
                branch.getId(),
                branch.getDescription(),
                branch.getPhoneNumber(),
                branch.getEmail(),
                branch.getAddress(),
                branch.getCity(),
                branch.getLatitude(),
                branch.getLongitude(),
                branch.getCheckInTime().toString(),
                branch.getCheckOutTime().toString(),
                branch.getStatus(),
                branch.getCreatedAt()
        );
    }
}
