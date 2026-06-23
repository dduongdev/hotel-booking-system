package com.dduongdev.hotel.payload.response;

import java.time.LocalDateTime;

import com.dduongdev.hotel.entity.Branch.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class CreateBranchResponse {
    private Long id;
    private String name;
    private String description;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private String checkInTime;
    private String checkOutTime;
    private Status status;
    private String imageUrl;
    private LocalDateTime createdAt;
}
