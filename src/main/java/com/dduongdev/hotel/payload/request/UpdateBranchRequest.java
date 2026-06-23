package com.dduongdev.hotel.payload.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.dduongdev.hotel.util.Constants;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateBranchRequest {

    @NotBlank(message = "Branch name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = Constants.PHONE_NUMBER_PATTERN, message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = Constants.EMAIL_PATTERN, message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "City cannot be blank")
    private String city;

    private Double latitude;

    private Double longitude;

    @NotNull(message = "Check-in time is required")
    private String checkInTime;

    @NotNull(message = "Check-out time is required")
    private String checkOutTime;

    private MultipartFile image;
}
