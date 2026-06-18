package com.dduongdev.hotel.payload.request;

import com.dduongdev.hotel.util.Constants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class VerifyOtpRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = Constants.PHONE_NUMBER_PATTERN, message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "OTP code is required")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    @Pattern(regexp = Constants.OTP_PATTERN, message = "OTP must contain only digits")
    private String otpCode;
}
