package com.dduongdev.hotel.payload.request;

import com.dduongdev.hotel.util.Constants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SendOtpRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = Constants.PHONE_NUMBER_PATTERN, message = "Invalid phone number format")
    private String phoneNumber;
}
